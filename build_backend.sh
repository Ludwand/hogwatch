#!/bin/bash

mkdir -p bin
kotlinc -classpath "backend/lib/postgresql-42.7.4.jar" -d bin/backend.jar backend/src/*.kt
