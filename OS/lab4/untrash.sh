#!/bin/bash

fileFound=0
TRASHDIR=$HOME/.trash/
TRASHLOG=$HOME/.trash.log

if [[ $# -ne 1 ]]
then
    echo "Error! Expected 1 argument, found $#"
    exit 1
fi

while read -r line
do
    ans=""
    origName=`awk -F / '{print $NF}' <<< $(cut -d ' ' -f 1 <<< $line)`
    name=$origName
    dir=`cut -d ' ' -f 1 <<< $line | awk -F / 'BEGIN{OFS="/"} {$NF=""; print $0}'`
    newName=`awk '{print $2}' <<< $line`
    if [[ $origName == $1 ]] && [[ -f $TRASHDIR$newName ]]
    then
        fileFound=1
        while [[ $ans != y ]] && [[ $ans != n ]]
        do
            echo "Do you want to restore $dir$origName? (y/n)"
            read ans<&1
        done
        if [[ $ans == y ]]
        then
            if [[ ! -d $dir ]] 
            then
                echo "Directory $dir doesn't exist. The file will be created in $HOME."
                dir="$HOME"
            fi
            while [[ -f $dir$name ]]
            do
                echo "$name already exists in $dir. What is the name of the file being restored?"
                read name<&1
            done
            ln $TRASHDIR$newName $dir$name
            rm -f $TRASHDIR$newName
            echo "File $dir$name created"
        fi
    fi
done <<< $(cat $TRASHLOG)

if [[ $fileFound -ne 1 ]]; then
  echo "Nothing to restore: no files with name '$1'"
  exit 1
fi
