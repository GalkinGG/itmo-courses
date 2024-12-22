#!/bin/bash

usrDir="$HOME"
restDir="$HOME/restore"

lastDate=$(awk -F - '{print $2"-"$3"-"$4}' <<< `ls $usrDir | grep -E "^Backup-[0-9]{4}-[0-1][0-9]-[0-3][0-9]$"` | sort | tail -n 1)

if [[ $lastDate == "--" ]]
then
    echo "Nothing to restore"
    exit 1
fi

bckpDir=$usrDir/Backup-$lastDate

if [[ ! -d $restDir ]]
then
  mkdir $restDir
fi

origFiles=$(for file in `ls $bckpDir`
do
    if [[ $file =~ .[0-9]{4}-[0-1][0-9]-[0-3][0-9] ]]
    then
        echo $(echo $file | awk -F '.[0-9]{4}-[0-1][0-9]-[0-3][0-9]' '{print $1}')
    else
        echo $file
    fi
done | sort | uniq)

for file in $origFiles
do
    name=`ls $bckpDir | grep "$file" | sort | tail -n 1`
    cp $bckpDir/$name $restDir/$file
done
