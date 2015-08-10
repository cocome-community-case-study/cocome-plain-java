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

package org.cocome.tradingsystem.inventory.data.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.cocome.tradingsystem.inventory.data.store.StockItem;
import org.cocome.tradingsystem.inventory.data.store.Store;
import org.cocome.tradingsystem.util.java.Maps;

/**
 * Utility class that fills the database and writes system configuration
 * to a file.
 * 
 * @author Lubomir Bulej
 */
final class DatabaseFiller {

	private enum ItemType {
		SHARED, UNIQUE;
	}

	//

	private static final class ItemInfo {
		final long barcode;
		final long amount;
		final ItemType type;

		ItemInfo(final long barcode, final long amount, final ItemType type) {
			this.barcode = barcode;
			this.amount = amount;
			this.type = type;
		}

		@Override
		public String toString() {
			return String.format("%d,%d,%s", barcode, amount, type);
		}
	}

	//

	static void fillDatabase(
			final DatabaseConfiguration databaseConfig, final DefaultEntityConfiguration entityConfig,
			final int cashDesksPerStore,
			final File outputDir
			) throws Exception {
		__printDatabaseConfiguration(databaseConfig);

		//

		final DatabaseContent content = __fillDatabase(
				databaseConfig, entityConfig
				);

		if (outputDir != null) {
			__writeSystemConfiguration(cashDesksPerStore, content, outputDir);
			__writeClientConfiguration(content, outputDir);
		}
	}

	private static void __printDatabaseConfiguration(final DatabaseConfiguration config) {
		final Formatter output = new Formatter(System.out);

		output.format("Database configuration:\n");
		output.format("\t%d enterprises\n", config.getEnterpriseCount());
		output.format(
				"\t%d suppliers (%d shared, %d unique)\n", config.getSupplierCount(),
				config.getSharedSupplierCount(), config.getUniqueSupplierCount()
				);
		output.format("\t%d stores\n", config.getStoreCount());
		output.format(
				"\t%d products (%d shared, %d unique)\n", config.getProductCount(),
				config.getSharedProductCount(), config.getUniqueProductCount()
				);
		output.format("\t%d stock items\n", config.getStockItemCount());
		output.format("\t%d product orders\n", config.getProductOrderCount());
		output.format("\t%d order entries\n", config.getOrderEntryCount());

		output.flush();
	}

	//

	private static DatabaseContent __fillDatabase(
			final DatabaseConfiguration databaseConfig,
			final DefaultEntityConfiguration entityConfig
			) {
		info("Generating database entities... ");
		final DatabaseContent entities =
				DatabaseGenerator.generate(databaseConfig, entityConfig);
		info("done\n");

		//

		info("Storing entities into database... ");
		entities.makePersistent();
		info("done\n");

		return entities;
	}

	private static void __writeSystemConfiguration(
			final int cashDesksPerStore, final DatabaseContent content,
			final File configDir
			) throws Exception {
		final Properties properties = __loadProperties(
				new File(configDir, "tradingsystem.properties.base")
				);

		// Create list of valid store sections
		final String tradingStores = __makeList("store", content.stores.size());
		properties.setProperty("trading.stores", tradingStores);

		// Create section for each store
		int storeNumber = 0;
		for (final Store store : content.stores) {
			final String configStore = "store" + storeNumber;

			properties.setProperty(configStore + ".name", store.getName());
			properties.setProperty(configStore + ".id", String.valueOf(store.getId()));
			properties.setProperty(configStore + ".console", String.valueOf(true));
			properties.setProperty(configStore + ".cashdeskui", String.valueOf(true));
			properties.setProperty(configStore + ".cashdesks", String.valueOf(cashDesksPerStore));

			storeNumber++;
		}

		__storeProperties(
				properties, new File(configDir, "tradingsystem.properties"));
	}

	private static String __makeList(final String prefix, final int count) {
		final StringBuilder result = new StringBuilder();
		for (int index = 0; index < count; index++) {
			result.append(prefix).append(index).append(',');
		}

		// delete last comma
		result.setLength(result.length() - 1);

		return result.toString();
	}

	private static void __writeClientConfiguration(
			final DatabaseContent content, final File outputDir
			) throws Exception {
		//
		// Collect stock item amounts and product barcodes for each store and
		// output them to a file for the client to read. Use store name as
		// indices into the map, because the disconnected Store entity could
		// exist in multiple instances.
		//
		final Map<String, List<ItemInfo>> storeItemInfos = Maps.newHashMap();
		for (final Store store : content.stores) {
			storeItemInfos.put(store.getName(), new ArrayList<ItemInfo>());
		}

		__collectItemInfos(content.sharedStockItems, ItemType.SHARED, storeItemInfos);
		__collectItemInfos(content.uniqueStockItems, ItemType.UNIQUE, storeItemInfos);

		//

		for (final Store store : content.stores) {
			__writeStringValues(
					storeItemInfos.get(store.getName()),
					new File(outputDir, "stockitems." + store.getId()));
		}
	}

	private static void __collectItemInfos(
			final List<StockItem> stockItems, final ItemType itemType,
			final Map<String, List<ItemInfo>> storeItemInfos
			) {
		for (final StockItem stockItem : stockItems) {
			final ItemInfo itemInfo = new ItemInfo(
					stockItem.getProduct().getBarcode(),
					stockItem.getAmount(),
					itemType
					);

			storeItemInfos.get(stockItem.getStore().getName()).add(itemInfo);
		}
	}

	static <T> void __writeStringValues(
			final Collection<T> elements, final File file
			) throws IOException {
		final PrintWriter writer = new PrintWriter(new FileWriter(file));
		try {
			for (final T element : elements) {
				writer.println(element.toString());
			}

		} finally {
			writer.close();
		}
	}

	//

	static Properties __loadProperties(
			final File file
			) throws IOException {
		final Properties result = new Properties();
		result.load(new FileInputStream(file));
		return result;
	}

	static void __storeProperties(
			final Properties properties, final File file
			) throws IOException {
		final FileOutputStream out = new FileOutputStream(file);
		try {
			properties.store(out, "");

		} finally {
			out.close();
		}
	}

	//
	// Helpers for command line parsing
	//

	static File getOutputDir(final String[] args, final int index) {
		if (args.length > index && !args[index].trim().isEmpty()) {
			final File outputDir = new File(args[index]);
			info(Boolean.toString(new File(".").isDirectory()));
			if (!outputDir.isDirectory()) {
				die("Invalid output directory: %s\n", outputDir);
			}

			return outputDir;

		} else {
			return null;
		}
	}

	static int getCashDesksPerStore(final String[] args, final int index) {
		if (args.length > index && !args[index].trim().isEmpty()) {
			return Integer.parseInt(args[index]);
		} else {
			return 0;
		}
	}

	//

	static void info(final String format, final Object... args) {
		System.out.printf(format, args);
		System.out.flush();
	}

	static void die(final String format, final Object... args) {
		System.err.printf(format, args);
		System.exit(1);
	}

}
