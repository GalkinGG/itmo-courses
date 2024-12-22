#!/bin/bash

ps ax -u "$USER" -o pid,stat | awk '$2 ~ /Z/ {print $1}'
