// Extend the pattern class
+ Pattern {
	// These methods work like the Pchain shortcut but they add and multiply
	// values instead of overriding them.
	// add Patterns
	<+ { arg aPattern;
		if (aPattern.isString) {
			aPattern = Pixi(aPattern);
		};
		aPattern.patternpairs.do{|e, i|
			if (e.class == Symbol) {
				^Padd(e, aPattern.patternpairs[i+1], this);
			} {
				nil
			}
		};
	}
	// multiply Patterns
	<* { arg aPattern;
		if (aPattern.isString) {
			aPattern = Pixi(aPattern);
		};
		aPattern.patternpairs.do{|e, i|
			if (e.class == Symbol) {
				^Pmul(e, aPattern.patternpairs[i+1], this);
			} {
				nil
			}
		};
	}

	// overriding original
	// compose Patterns
	<> { arg aPattern;
		if (aPattern.isString) {
			aPattern = Pixi(aPattern);
		};
		^Pchain(this, aPattern)
	}

	// overriding original
	// *new { arg anything;
	// 	if (anything.isString) {
	// 		^Pixi(anything);
	// 	};
	// }

	pclump { arg n;
		^Pclump(n, this);
	}

	pavaroh { arg aroh, avaroh, stepsPerOctave=12;
		^Pavaroh(this, aroh, avaroh, stepsPerOctave);
	}

	prorate { arg proportion;
		^Prorate(proportion, this);
	}

	pn {arg repeats=inf, key;
		^Pn(this, repeats, key);
	}

	pstutter {arg n;
		^Pstutter(n, this);
	}
}

