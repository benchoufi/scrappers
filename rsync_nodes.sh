#!/usr/bin/env bash
set -e


NUMBER_OF_ARGS=${#@}
NUMBER_OF_NODES=0
SYNC_DIR=""
USER_NAME=""

error_exit()
{
	echo "$1" 1>&2
	exit 1
}

if [ "$NUMBER_OF_ARGS" -ne 6 ] ; then
	error_exit "Incorrect number of parameters"
fi

ARGS=`getopt h:n:u:d: $*`


eval set -- "$ARGS"

tmp=0

while true; do
	echo "$1" 
	case "$1" in
    -h|--help)
      shift;
      if [ -n "$1" ]; then
        echo "Help us and contribute on our GitHub account";
        shift;
      fi
      ;;
    -n|--nodes)
	  shift;
      if [ -n "$1" ]; then
		tmp=$((tmp+1))
		NUMBER_OF_NODES="$1"
        echo "number of synchronized nodes : $1";
        shift;
      fi
      ;;
	-d|--dir)
	  shift;
	  if [ -n "$1" ]; then
	  	tmp=$((tmp+1))
		SYNC_DIR="$1"
	 	echo "syncronized directories : $1";
	  	shift;
      fi
	  ;;
  	-u|--username)
  	  shift;
  	  if [ -n "$1" ]; then
  	  	tmp=$((tmp+1))
		USER_NAME="$1"
  	  	echo "user name is : $1";
  	  	shift;
      fi
  	  ;;
	  --)
      shift;
      break;
      ;;
  esac
done

if [ "$tmp" -ne 3 ] ; then
	error_exit "Incorrect flags" 
fi

prepare()
{
	rsync --write-batch=_rsync -avzP /home/"$USER_NAME"/"$SYNC_DIR" "$USER_NAME"@metamap-w-0:/home/"$USER_NAME"
}

synchronize() 
{
	for i in $(seq 0 $((NUMBER_OF_NODES-1)))
	do
	   ssh metamap-w-"$i" rsync --read-batch=- -a /home/"$USER_NAME" < _rsync
	   #"_$SYNC_DIR".sh "$USER_NAME"@metamap-w-"$i":/home/"$USER_NAME"/"$SYNC_DIR"
	done
	exit 0
}

prepare
synchronize 