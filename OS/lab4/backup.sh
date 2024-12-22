#!/bin/bash

function now() {
  date +%d.%m.%Y\ %H:%M:%S
}

function fileSize() {
  wc -c $1 | awk '{print $1}'
}

usrDIR="$HOME"
srcDIR="$HOME/source"
reportFile="$HOME/backup-report"

actualDate=`date +%Y-%m-%d`
lastDate=$(awk -F - '{print $2"-"$3"-"$4}' <<< `ls $usrDIR | grep -E "^Backup-[0-9]{4}-[0-1][0-9]-[0-3][0-9]$"` | sort | tail -n 1)
let actualVersion=$(date +%s -d $actualDate)-7*24*60*60
lastVersion=$(date +%s -d $lastDate)

if [[ ! -f $reportFile ]]
then
  touch $reportFile
fi

if [[ $lastDate == "--" ]] || [[ $lastVersion -lt $actualVersion ]]
then
    newDir=$usrDIR/Backup-$actualDate
    mkdir $newDir && cp $srcDIR/* $newDir
    echo "Backup done $(now) in directory $newDir"
    echo "Added: "
    ls $newDir | awk '{print " + "$0}'
    echo
else
    bckpDir=$usrDIR/Backup-$lastDate
    echo "  Backup updated $(now) in directory $bckpDir"
    echo "Added: "
    for file in `ls $srcDIR`
    do
        if [[ ! -f $bckpDir/$file ]]
        then
            cp $srcDIR/$file $bckpDir/$file
            echo " + $file"
        fi
    done
    echo "Modified: "
    for file in `ls $srcDIR`
    do
        lastV=$(ls $bckpDir | grep -E "^$file.[0-9]{4}-[0-1][0-9]-[0-3][0-9]$" | sort | tail -n 1)
        if [[ -z $lastV ]]
        then
            lastV=$file
        fi

        if [[ $(fileSize $bckpDir/$lastV) -ne $(fileSize $srcDIR/$file) ]]; then
            cp $srcDIR/$file $bckpDir/$lastV.$actualDate
            echo " M $lastV $lastV.$actualDate"
        fi
    done
    echo
fi >> $reportFile
