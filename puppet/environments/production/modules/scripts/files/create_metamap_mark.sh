#!/usr/bin/env bash
set -e

if [[ ! -e $1 ]]; then
	mkdir $1
else
	echo "directory already created"
fi
