+ String {
	asDegrees {
		^this.digit.collect(_ ? \r); // replace nil by \r
	}

	pixi { arg repeats=1;
		^Pixi(this, repeats);
	}

	pseq{ arg repeats=1, offset=0;
		^Pseq(this, repeats, offset);
	}
}