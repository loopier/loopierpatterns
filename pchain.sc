+Pchain {
	// find { arg key;
	// 	patternpairs.pairsDo { |u,x,i|
	// 		if(u == key) { ^i }
	// 	};
	// 	^nil
	// }

	// set { arg ...args;
	// 	args.pairsDo { |key, val|
	// 		var i = this.find(key);
	// 		if (i.notNil) {
	// 			if (val.isNil) {
	// 				patternpairs.removeAt(i);
	// 				patternpairs.removeAt(i);
	// 			} {
	// 				patternpairs[i+1] = val
	// 			}
	// 		}{
	// 			patternpairs = patternpairs ++ [key, val];
	// 		}
	// 	}
	// }

	// map all methods that are not understood to a Pbind parameter
	doesNotUnderstand { |selector ...args|
		selector.debug("Pchain doesNotUnderstand");
		^Pchain(Pbind(selector, args[0]), this);
	}

	deg { |value| ^Pchain(Pbind(\degree, value), this) }

	fastest { ^Pchain(Pbind(\dur, 1/8), this)}
	faster { ^Pchain(Pbind(\dur, 1/4), this)}
	fast { ^Pchain(Pbind(\dur, 1/2), this)}
	slow { ^Pchain(Pbind(\dur, 2), this)}
	slower { ^Pchain(Pbind(\dur, 4), this)}
	slowest { ^Pchain(Pbind(\dur, 8), this)}

	lowest { ^Pchain(Pbind(\octave, 2), this)}
	lower { ^Pchain(Pbind(\octave, 3), this)}
	low { ^Pchain(Pbind(\octave, 4), this)}
	high { ^Pchain(Pbind(\octave, 6), this)}
	higher { ^Pchain(Pbind(\octave, 7), this)}
	highest { ^Pchain(Pbind(\octave, 8), this)}

	fff { ^Pchain(Pbind(\amp, 2), this)}
	ff { ^Pchain(Pbind(\amp, 1), this)}
	f { ^Pchain(Pbind(\amp, 0.5), this)}
	p { ^Pchain(Pbind(\amp, 0.3), this)}
	pp { ^Pchain(Pbind(\amp, 0.2), this)}
	ppp { ^Pchain(Pbind(\amp, 0.1), this)}

	bjorklund { |k,n|
		^Pchain(Pbind(\bj, Pseq(Bjorklund(k,n).replace(0,\r), inf)), this);
	}

	bj { |k,n|
		^this.bjorklund(k,n);
	}

	pedal { ^Pchain(Pbind(\legato, 4), this)}
	leg { ^Pchain(Pbind(\legato, 1), this)}
	pizz { ^Pchain(Pbind(\legato, 0.5), this)}
	stacc { ^Pchain(Pbind(\legato, 0.1), this)}
}
