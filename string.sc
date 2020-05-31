+ String {
	pixi { arg repeats=1;
		^Pixi(this, repeats);
	}

	pseq{ arg repeats=1, offset=0;
		^Pseq(this, repeats, offset);
	}
}