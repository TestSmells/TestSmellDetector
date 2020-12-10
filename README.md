# Test Smell Detector

## Introduction

Unit test code, just like any other source code, is subject to bad programming practices, known also as anti-patterns, defects and smells. Smells, being symptoms of bad design or implementation decisions, has been proven to be responsible for decreasing the quality of software systems from various aspects, such as making it harder to understand, more complex to maintain, more prone to errors and bugs.

Test smells are defined as bad programming practices in unit test code (such as how test cases are organized, implemented and interact with each other) that indicate potential design problems in the test source code.

## Project Overview

The purpose of this project is twofold:

1. Contribute to the list of existing test smells, by proposing new test smells that developers need to be aware of.
2. Provide developers with a tool to automatically detect test smell in their unit test code. 

## More Information

Visit the project website: https://testsmells.github.io/

## Execution

Running the jar with `--help` will print its usage.

```
Options:
  -f, --file PATH                  The csv input file
  -t, --thresholds [default|spadini]
                                   The threshold to use for the detection
  -g, --granularity [boolean|numerical]
                                   Boolean value of numerical for the
                                   detection
```