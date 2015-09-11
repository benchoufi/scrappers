#!/usr/bin/env bash
set -e
#. $(dirname $0)/hadoop-env-setup.sh


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
BASE_URL="sameditresfroid.fr/"
BASE_NAME="public_mm"
EXTENSION_ZIP_FILE=".tar.bz2"
# construct URL
FILE_NAME="$BASE_NAME"_"$OS"_main_"$YEAR$EXTENSION_ZIP_FILE"
URL=$BASE_URL$FILE_NAME
PID=$$

mk_dir()
{
	if mkdir $1 ; then
		echo "directory successfully created"
	else
		error_exit "Failed to create directory"
	fi
}

mv_file()
{
	if mv $1 $2 ; then
		echo "succcessfully moved file inside its directory"
	else
		error_exit "Failed to move file inside its directory"
	fi
}

change_dir()
{
	if cd $1 ; then
		echo $1
		pwd
		echo "Successfully changed directory"
	else
		error_exit "Failed to change directory"
	fi
}

gs_download_metamap()
{
	/home/hadoop/hadoop-install/bin/hadoop dfs -get "/$METAMAP_DIR/"$FILE_NAME .
}

download_metamap()
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

unzip_file()
{
	bunzip2 -c "$FILE_NAME" | tar xvf -
}

export_path()
{
	export PATH=~/"$BASE_NAME"/bin:$PATH
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
	mk_dir $METAMAP_DIR
	change_dir $METAMAP_DIR
	gs_download_metamap 
	unzip_file
	change_dir $BASE_NAME
	export_path
	run_install `pwd`
	exit 0
}

run