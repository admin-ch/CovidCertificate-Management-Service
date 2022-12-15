#!/bin/bash

# collects all flyway migration scripts (up to given version and without excluded ones),
# sorts them and concatenates their contents to one single file

if [ $# -lt 1 ]
then
   echo "Aborted - Parameters missing:"
   echo " ./concat-versions.sh -u [version] -e [exclude]"
   echo " -u [version]:  stops collecting files when a file matches this version number (optional)"
   echo " -e [exclude]:  excludes matching files from being collected (optional)"
   echo "example:  ./concat-versions.sh -u V1_0_68 -e /postgresql/"
   exit 1
fi

uptoVersion="*ALL*"
excludeFiles="*NONE*"

while (( "$#" ))
do
   case "$1" in
      -u|--upto)
        if [[ -n "$2" ]]
        then
          uptoVersion="${2}"
          shift 2
        else
          echo "Aborted - Missing value for '-u' parameter"
          exit 1
        fi
        ;;
      -e|--exclude)
        if [[ -n "$2" ]]
        then
          excludeFiles="${2}"
          shift 2
        else
          echo "Aborted - Missing value for '-e' parameter"
          exit 1
        fi
        ;;
      -*|--*=) # unsupported flags
        echo "Error: Unsupported flag $1" >&2
        exit 1
        ;;
  esac
done

# find all included flyway migration files
find ../src -name V*.sql | grep -v "${excludeFiles}" > migrations.txt

# extract the filenames and ignore the path
rm -f versions.txt
while read m; do
  basename $m >> versions.txt
done <migrations.txt
rm migrations.txt

# sort the filenames in ascending order
sort --version-sort -f versions.txt > versions-sorted.txt
rm versions.txt

# go thru the sorted versions and concat contents
rm -f concated-versions.sql
while read v; do

  # stop when given version is detected
  if [[ $v == $uptoVersion* ]];
  then
    break
  fi

  filename=`find ../src -name $v`
  echo "adding $v"
  echo "" >> concated-versions.sql
  echo "" >> concated-versions.sql
  echo "-- VERSION $v" >> concated-versions.sql
  less $filename >> concated-versions.sql

done <versions-sorted.txt
rm versions-sorted.txt
