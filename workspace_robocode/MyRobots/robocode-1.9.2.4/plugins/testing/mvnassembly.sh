#!/bin/bash
#
# Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://robocode.sourceforge.net/license/epl-v10.html
#

./mvn.sh clean install ant:ant -DskipTests=false $*
#./mvn.sh eclipse:eclipse
