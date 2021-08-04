+Pbind {
	set { |...pairs|
		var newpairs;
		newpairs = merge(
			pairs.asDict,
			this.patternpairs.asDict,
			{|new, old| new ? old}
		).asPairs;
		^Pbind(*newpairs);
	}

	// map all methods that are not understood to a Pbind parameter
	doesNotUnderstand { |selector ...args|
		selector.debug("Pbind doesNotUnderstand");
		^this.set(selector, args[0]);
	}

	fastest { ^this.set(\dur, 1/8) }
	faster { ^this.set(\dur, 1/4) }
	fast { ^this.set(\dur, 1/2) }
	slow { ^this.set(\dur, 2) }
	slower { ^this.set(\dur, 4) }
	slowest { ^this.set(\dur, 8) }

	lowest { ^this.set(\octave, 2) }
	lower { ^this.set(\octave, 3) }
	low { ^this.set(\octave, 4) }
	high { ^this.set(\octave, 6) }
	higher { ^this.set(\octave, 7) }
	highest { ^this.set(\octave, 8) }

	fff { ^this.set(\amp, 2) }
	ff { ^this.set(\amp, 1) }
	f { ^this.set(\amp, 0.5) }
	p { ^this.set(\amp, 0.3) }
	pp { ^this.set(\amp, 0.2) }
	ppp { ^this.set(\amp, 0.1) }

	bjorklund { |k,n|
		^this.set(\bj, Pseq(Bjorklund(k,n).replace(0,\r), inf));
	}

	bj { |k,n|
		^this.bjorklund(k,n);
	}

	pedal { ^this.set(\legato, 4) }
	legato { ^this.set(\legato, 1) }
	pizz { ^this.set(\legato, 0.5) }
	stacc { ^this.set(\legato, 0.1) }
}
