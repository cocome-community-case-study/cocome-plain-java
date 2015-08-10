/***************************************************************************
 * Copyright 2013 DFG SPP 1593 (http://dfg-spp1593.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package org.cocome.tradingsystem.inventory.application.productdispatcher;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Utility class for executing the AMPL solver backend as a local process. When
 * the process is running, commands can be sent to it and the response can be
 * read back. This is a cleaned up version of the original {@link org.netlib.ampl.Ampl Ampl} class.
 * 
 * @author Lubomir Bulej
 */
final class AmplPipe {

	/** Last prompt issued by the AMPL solver backend. */
	private String __prompt = "";

	/** Buffer holding the text output by the AMPL solver backend. */
	private StringBuilder __buffer = new StringBuilder();

	/** Input of the AMPL solver backend. */
	private final PrintStream __amplInput;

	/** Output of the AMPL solver backend. */
	private final DataInputStream __amplOutput;

	/** The running AMPL solver process. */
	private final Process __process;

	//

	private AmplPipe(final Process process) {
		__process = process;

		//
		// Hook into the process input and output streams.
		//
		__amplInput = new PrintStream(process.getOutputStream(), true);
		__amplOutput = new DataInputStream(process.getInputStream());
	}

	//

	public static AmplPipe open(final File amplExecutable) throws IOException {

		final String[] command = new String[] {
			// path to the executable
			amplExecutable.toString(),

			// block mode: turns off carriage returns
			"-b"
		};

		//
		// Execute the command in a local process.
		//
		final Runtime rt = Runtime.getRuntime();
		final Process process = rt.exec(command);
		final AmplPipe ampl = new AmplPipe(process);

		//
		// Swallow initial prompt from AMPL
		//
		ampl.receive();
		return ampl;
	}

	public String getPrompt() {
		return __prompt;
	}

	public void send(final String text) {
		//
		// Send text to AMPL in block mode, which means to prefix it with the
		// length of the text to receive.
		//
		if (!text.isEmpty()) {
			__amplInput.print(text.length());
			__amplInput.print(" ");
			__amplInput.print(text);
		}
	}

	public String receive() throws IOException {
		__buffer.setLength(0);

		//
		// Read the output from AMPL in blocks. Each block is preceded by a string
		// representing its length, separated by a space from the contents of the
		// block.
		//
		RECEIVE: while (true) {
			final int blockLength = __readLength();
			if (blockLength == 0) {
				break RECEIVE;
			}

			final int blockStart = __bufferBlock(blockLength);

			//
			// If there is a prompt, keep the prompt string aside and
			// discard the last block from the buffer and stop reading.
			//
			final int promptIndex = __buffer.indexOf("prompt", blockStart);
			if (promptIndex == blockStart) {
				final int promptStart = __buffer.indexOf("\n", blockStart);
				__prompt = __buffer.substring(promptStart + 1);

				__buffer.setLength(blockStart);
				break RECEIVE;
			}
		}

		//
		// Return the contents of the buffer up to the first end of line.
		//
		final int startEol = __buffer.indexOf("\n");
		return __buffer.substring(startEol + 1);
	}

	private int __readLength() throws IOException {
		int length = 0;

		while (true) {
			final int ch = __amplOutput.read();

			if (Character.isDigit(ch)) {
				// digit, update length
				length = 10 * length + Character.getNumericValue(ch);

			} else if (ch == -1 && length > 0) {
				// no more characters while reading length
				throw new IOException(
						"Unexpected end of stream while reading block length");

			} else {
				// non-digit character, return the length
				return length;
			}
		}
	}

	private int __bufferBlock(final int blockLength) throws IOException {
		final int blockStart = __buffer.length();

		for (int bytesRead = 0; bytesRead < blockLength; bytesRead++) {
			final int ch = __amplOutput.read();

			if (ch == -1) {
				throw new IOException(String.format(
						"Unexpected end of stream while reading block: read %d, expected %d\n%s ",
						bytesRead, blockLength, __buffer.substring(blockStart)
						));

			} else if (ch != '\r') {
				// put characters (except carriage returns) into buffer
				__buffer.appendCodePoint(ch);
			}
		}

		return blockStart;
	}

	public int close() throws InterruptedException {
		//
		// Close the streams used to communicate with the AMPL
		// solver process and terminate the process. Returns
		// the exit value returned by the AMPL process.
		//
		try {
			__amplInput.close();
			__amplOutput.close();

		} catch (final IOException ioe) {
			// ignore exceptions when closing the streams

		} finally {
			// kill the process at the end
			__process.destroy();
		}

		return __process.waitFor();
	}

}
