# orb

Floating orb soundscape generative light responding entity

## Usage

> lein repl

## DSL for minimal audio language

* delay
* compose
* mix
* high/low-pass filter
* waveshape
* fft rolling buffer

Example:




insert graph sine B output->A:amplitude
parameter B:amplitude<-0.5
parameter B:frequency<-1.5
parameter B:phase<-0

---------------------------

feature detectors can be generators
feature detectors are translated into tokens for the graph language


## License

Copyright © 2018 Ryan Spangler

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
