#!/bin/bash

# load setup
. deployment.rc
. tradingsystem.rc

./start-script.sh org.cocome.tradingsystem.external.BankLauncherMain "Bank.$bank_name" "$rmiregistry_host" "$rmiregistry_port" "$bank_name"

# end

