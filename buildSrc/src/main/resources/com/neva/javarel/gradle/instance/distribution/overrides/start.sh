#!/bin/bash

java -agentlib:jdwp=transport=dt_socket,server=y,suspend={{config.debug ? "y": "n"}},address={{instance.debugPort}} -jar bin/felix.jar