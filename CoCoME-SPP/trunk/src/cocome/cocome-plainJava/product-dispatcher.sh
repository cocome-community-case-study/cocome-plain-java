#!/bin/bash

# load setup
. deployment.rc
. tradingsystem.rc

./start-script.sh org.cocome.tradingsystem.inventory.application.productdispatcher.ProductDispatcherLauncherMain "ProductDispatcher.${dispatcher_name}" "$rmiregistry_host" "$rmiregistry_port" "${dispatcher_name}"

# end

