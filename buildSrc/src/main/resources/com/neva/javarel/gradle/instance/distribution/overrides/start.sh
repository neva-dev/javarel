#!/bin/bash

java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address={{instance.debugPort}} -jar bin/felix.jar