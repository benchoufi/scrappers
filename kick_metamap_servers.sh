#!/usr/bin/env bash
set -e

NUMBER_OF_ARGS=${#@}

error_exit()
{
	echo "$1" 1>&2
	exit 1
}

if [ "$NUMBER_OF_ARGS" -lt 1 ] ; then
	error_exit "Incorrect number of parameters"
fi

ARGS=`getopt h:y:d: $*`


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

if [ "$tmp" -ne 1 ] ; then
	error_exit "Incorrect flags" 
fi

SUBSTRING_YEAR=${YEAR:2:2}
METAMAP_DIR="metamap"
BASE_NAME="public_mm"
# check wether start or stop servers
ACTION="${@: -1}"

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

start_servers()
{
	if ./bin/skrmedpostctl $1 ; then
		echo "SKR/Medpost Tagger successfully reached"
		if ./bin/wsdserverctl $1 ; then
			echo "Word Sense Disambiguation Server successfully reached"
			if [ $1 == "start" ]
				if ./bin/mmserver"$SUBSTRING_YEAR"; then
					echo "MM Server successfully reached"
				else
					error_exit "MM Server failed reaching"
				fi
			fi
		else
			error_exit "Word Sense Disambiguation (WSD) failed reaching"
		fi
	else
		error_exit "SKR/Medpost Tagger failed reaching"
	fi	
}


run()
{
	change_dir $METAMAP_DIR
	change_dir $BASE_NAME
	start_servers $ACTION
	exit 0
}

run