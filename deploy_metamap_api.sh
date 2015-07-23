#!/usr/bin/env bash
set -e

NUMBER_OF_ARGS=${#@}

error_exit()
{
	echo "$1" 1>&2
	exit 1
}

if [ "$NUMBER_OF_ARGS" -lt 2 ] ; then
	error_exit "Incorrect number of parameters"
fi

ARGS=`getopt h:o:y:d: $*`


eval set -- "$ARGS"

tmp=0

while true; do
  case "$1" in
    -h|--help)
      shift;
      if [ -n "$1" ]; then
        echo "Help us and contribute on our GitHub account";
        shift;
      fi
      ;;
    -o|--os)
	  shift;
      if [ -n "$1" ]; then
		tmp=$((tmp+1))
		OS=$1
        echo "OS version : $1";
        shift;
      fi
      ;;
    -y|--year)
      shift;
      if [ -n "$1" ]; then
		tmp=$((tmp+1))
		YEAR=$1
        echo "Year released : $1";
        shift;
      fi
      ;;
    --)
      shift;
      break;
      ;;
  esac
done

if [ "$tmp" -ne 2 ] ; then
	error_exit "Incorrect flags" 
fi

METAMAP_DIR="metamap"
BASE_NAME="public_mm"
EXTENSION_ZIP_FILE=".tar.bz2"
# construct file name
FILE_NAME="$BASE_NAME"_"$OS"_javaapi_"$YEAR$EXTENSION_ZIP_FILE"

change_dir()
{
	if cd "$1" ; then
		echo "$1"
		pwd
		echo "Successfully changed directory"
	else
		error_exit "Failed to change directory"
	fi
}

gs_download_metamap()
{
	hadoop dfs -get "/$METAMAP_DIR/"$FILE_NAME .
}

unzip_file()
{
	bzip2 -dc $FILE_NAME | tar xvf -
}

run_install()
{	
	if echo $1 | ./bin/install.sh ; then
		echo "Installation processing"
	else
		error_exit "Installation failed"
	fi
}


run()
{
	change_dir $METAMAP_DIR
	gs_download_metamap 
	unzip_file
	change_dir $BASE_NAME
	run_install `pwd`
	exit 0
}

run