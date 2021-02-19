+Event {
	// converts to pbind -- shorter sintax
	asPbind {
		var pbind = Pbind();
		pbind.patternpairs = this.getPairs;
		^pbind
	}

	pb {
		^this.asPbind
	}

	p {
		^this.asPbind
	}

	// Pdef
	instrument { arg name;
		^this.asPdef(name);
	}

	ins { arg name;
		^this.asPdef(name);
	}

	inst { arg name;
		^this.asPdef(name);
	}

	pd { arg name;
		^this.asPdef(name);
	}

	asPdef { arg name;
		^Pdef(name.asSymbol, this.asPbind);
	}
}

