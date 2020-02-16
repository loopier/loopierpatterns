// A Pbind that uses strings to generate patterns.
Pixi : Pbind {
	classvar ops;
	classvar letters = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	*new { arg sequence;
		var degrees, durs, bufs, amps, legatos;
		var bars = sequence.split($|);
		degrees = this.degrees(bars);
		durs = this.durs(bars);
		// For buffers to work, the Pbind must have these pairs declared
		//
		// \samplelist, [ARRAYOFBUFFERS],
		// \samplenum, [INDEXOFSAMPLE],
		// \buf, Pfunc{|e| e[\samplelist][e[\samplenum]];},
		//
		// then you can chain it to a Pixi.  For example:
		// "oxox".pixi <> Pbind(\samplelist, b["kick"], \samplenum, ..., \buf, Pfunc..)
		bufs = this.bufs(bars);


		^super.newCopyArgs([
			// \instrument, \playbuf,
			\degree, degrees,
			\dur, durs,
			\samplenum, bufs,
			\buf, Pfunc{|e|
				var index;
				var list = e[\samplelist];
				if (list.isNil.not) {
					// list.debug("list");
					index = e[\samplenum].mod(e[\samplelist].size);
					list[index];
				} { 0 };
			},
			// \buf, bufs,
			// \amp, amps,
			// \legato, legatos,
		]);
	}

	// \brief Converts string of numbers to a pattern of degrees
	*degrees { arg sequenceStr;
		var degrees = List.new;
		sequenceStr.do { |seqStr|
			var seq = List.new;
			seqStr.do { |char|
				if (char.asString == " ") {
					seq.add(\rest);
				}{
					seq.add(char.asString.asInteger);
				}
			};
			if (seq.isEmpty) {
				seq.add(\rest);
			};
			// seq.debug("deg");
			degrees.add(Pseq(seq));
		};
		^Pseq(degrees);
	}

	// \brief Converts a string of alphanumerical characters to a pattern of durations,
	// dividing the number of items in the string in one single beat.
	*durs { arg sequenceStr;
		var pattern = "[0-9A-Za-z ]";
		var durs = List.new;
		sequenceStr.do { |seqStr|
			var regex = seqStr.findRegexp(pattern);
			var seq = (1/regex.size).dup(regex.size);
			if (seq.isEmpty) {
				seq.add(1);
			};
			// seq.debug("dur");
			durs.add(Pseq(seq));
		};
		// durs.debug("durs");
		^Pseq(durs);
	}

	// \brief Converts a string of alphabetical characters to a pattern of numbers.
	*bufs { arg sequenceStr;
		// var pattern = "[A-Za-z ]";
		var bufs = List.new;
		sequenceStr.do { |seqStr|
			var seq = this.lettersToIndices(seqStr);
			if (seq.isEmpty) {
				seq.add(0);
			};
			// seq.debug("bufs");
			bufs.add(Pseq(seq));
		};
		// durs.debug("durs");
		^Pseq(bufs);
	}

	// \brief Converts an alphabetical string to an array of indices of the letters
	*lettersToIndices { |str|
		var indices = List.new;
		str.do { |c|
			var index = letters.find(c);
			if (c.asString == " " || index.isNil) {
				indices.add(0);
			} {
				indices.add(index-1);
			}
		};
		^indices;
	}
}