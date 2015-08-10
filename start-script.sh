#!/bin/bash

# $1 = MAINCLASS
# $2 = LOGNAME

if [ "$1" != "" ] ; then
	MAINCLASS=$1
else
	echo "Main class missing."
	exit
fi

shift

if [ "$1" != "" ] ; then
	LOGNAME=$1
else
	echo "logname missing."
	exit
fi


shift

# setup

export COCOME="/home/rju/Projects/CoCoME/CoCoME-SPP/trunk/src/cocome-impl"

# load setup
. deployment.rc
. tradingsystem.rc

# determine classpath
export CLASSPATH=$COCOME/dist/:$COCOME/rsc/:/home/rju/aspectj1.7/lib/aspectjrt.jar:/home/rju/Projects/Kieker/kieker/dist/kieker-1.8-SNAPSHOT_aspectj.jar

for I in `find lib -name '*.jar'` ; do
export CLASSPATH="$CLASSPATH:$I"
done

# create directories
mkdir $log_dir
mkdir $status_dir

# absolute paths
export absolute_log_dir="$COCOME/$log_dir"
export absolute_status_dir="$COCOME/$status_dir"
export absolute_solver_dir="$COCOME/$solver_dir"
export absolute_solver_var_dir="$COCOME/$solver_var_dir"
export absolute_security_policy="$COCOME/$security_policy"

# sys parameter
export policy="-Djava.security.policy=$absolute_security_policy"
export log="-Dapplication.log.file=$absolute_log_dir/$LOGNAME.log"
export status="-Dapplication.status.dir=$absolute_status_dir"
export jmxremote="-Dcom.sun.management.jmxremote=true"
export ampl_home="-Dampl.home=${absolute_solver_dir}"
export ampl_data="-Dampl.data=${absolute_solver_var_dir}"

# execute
java -cp $CLASSPATH $policy $log $status $ampl_home $ampl_data $MAINCLASS $@

# end
