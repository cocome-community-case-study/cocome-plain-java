#!/bin/bash

if [ "$1" != "" ] ; then
	INPATH=$1
else
	echo "In path missing"
	exit
fi

if [ "$2" != "" ] ; then
	OUTFILE=$2
else
	echo "Out file missing"
	exit
fi

export CLASSPATH=/home/rju/aspectj1.7/lib/aspectjrt.jar

for I in `find lib -name '*.jar'` ; do
	export CLASSPATH="$CLASSPATH:$I"
done

for I in `find /home/rju/Projects/Kieker/kieker/lib -name '*.jar'` ; do
	export CLASSPATH="$CLASSPATH:$I"
done


export TEMP=ajc/classes
export KIEKER="/home/rju/Projects/Kieker/kieker/dist/kieker-1.8-SNAPSHOT_aspectj.jar"

export CLASSPATH="$CLASSPATH:$KIEKER"

$HOME/aspectj1.7/bin/ajc -inpath $INPATH -aspectpath $KIEKER -outjar $OUTFILE -1.5 -d $TEMP \
 -Xjoinpoints:synchronization

#end
