#!/usr/bin/env bash
set -e
#. $(dirname $0)/hadoop-env-setup.sh


NUMBER_OF_ARGS=${#@}

if [ "$NUMBER_OF_ARGS" -lt 2 ] ; then
	error_exit "Incorrect number of parameters"
fi

# ARGS=`getopt hoy:d $*`
#
#
# set -- "$ARGS"
#
# while true; do
#   case "$1" in
#     -h|--help)
#       shift;
#       if [ -n "$1" ]; then
#         echo "Help us and contribute on our GitHub account";
#         shift;
#       fi
#       ;;
#     -o|--os)
# 	  shift;
#       if [ -n "$1" ]; then
#         echo "OS version : $1";
#         shift;
#       fi
#       ;;
#     -y|--year)
#       shift;
#       echo "Year released : $1s";
#       ;;
#     --)
#       shift;
#       break;
#       ;;
#   esac
# done

OS=$0
YEAR=$1
METAMAP_DIR="metamap"
BASE_URL="sameditresfroid.fr/"
BASE_NAME="public_mm"
EXTENSION_ZIP_FILE=".tar.bz2"
# construct URL
FILE_NAME="$BASE_NAME"_$1_main_$2"$EXTENSION_ZIP_FILE"
URL=$BASE_URL$FILE_NAME
PID=$$

function mk_dir
{
	if mkdir $1 ; then
		echo "directory successfully created"
	else
		error_exit "Failed to create directory"
	fi
}

function mv_file
{
	if mv "$1" "$2" ; then
		echo "succcessfully moved file inside its directory"
	else
		error_exit "Failed to move file inside its directory"
	fi
}

function change_dir
{
	if cd "$1" ; then
		echo "$1"
		pwd
		echo "Successfully changed directory"
	else
		error_exit "Failed to change directory"
	fi
}

function download_metamap
{
	curl -O "$URL"
	lsof -o0 -o -p $PID |
	awk '
	            BEGIN { CONVFMT = "%.2f" }
	            $4 ~ /^[0-9]+r$/ && $7 ~ /^0t/ {
	                    offset = substr($7, 3)
	                    fname = $9
	                    "stat -f %z '\''" fname "'\''" | getline
	                    len = $0
	                    print fname, offset / len * 100 "%"
	            }
	    '
}

function unzip_file
{
	`bunzip2 -c "$FILE_NAME" | tar xvf - `
}

function export_path
{
	export PATH=~/"$BASE_NAME"/bin:$PATH
}

function run_install
{	
	if echo $1 | ./bin/install.sh ; then
		echo "Installation processing"
	else
		error_exit "Installation failed"
	fi
}

function start_medpost_server
{
	if ./bin/skrmedpostctl start; then
		echo "Starting MEDPOST server"
	else
		error_exit "Failed starting MEDPOST server"
	fi 
}

function start_wsd_server
{
	if ./bin/wsdserverctl start; then
		echo "Starting WSD server"
	else
		error_exit "Failed starting WSD server"
	fi
}

function run 
{
	#mk_dir $METAMAP_DIR
	#change_dir $METAMAP_DIR
	download_metamap 
	#unzip_file
	#change_dir $BASE_NAME
	#export_path
	#run_install `pwd`
	#start_medpost_server
	#start_wsd_server
	exit 0
}

function error_exit
{
	echo "$1" 1>&2
	exit 1
}

run