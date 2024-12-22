#!/bin/bash

ps ax -u "$USER" -o pid,command | tail -n +2 | wc -l > file1
ps ax -u "$USER" -o pid,command | tail -n +2 | awk '{print $1 ":" $2}' >> file1
