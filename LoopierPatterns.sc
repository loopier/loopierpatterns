// Cycles over a list of values, then it stays on the last value indefinately.
// Same as Pseq([0, Pseq([1],inf)])
Pline : Pseq {
	*new { arg list, repeats=1;
		// list[list.size - 1] = Pseq([list[list.size - 1]],inf);
		var last = list[list.size - 1];
		list = [Pseq(list, repeats), Pseq([last], inf)];
		^super.newCopyArgs(list, 1, 0);
	}
}

// Random sequence pattern.
// Generates a random sequence from the given list.
Pchoose : Pseq {
	var <>length;

	*new { arg size=0, list, repeats=1;
		if (size<1) { size = list.size };
		^super.newCopyArgs(list, repeats, 0, size);
	}

	embedInStream { arg inval;
		var item, stream;
		var localList = Array.fill(length, {list.choose});

		repeats.value(inval).do({ arg j;
			localList.size.do({ arg i;
				item = localList.wrapAt(i);
				inval = item.embedInStream(inval);
			});
		});
		^inval;
	}
}

// Pchoose : Pseq {
// 	var <>length;
//
// 	*new { arg size=0, list, repeats=1;
// 		if (size<1) { size = list.size };
// 		list = Array.fill(size, {list.choose});
// 		list.postln;
// 		^super.newCopyArgs(list, repeats, 0, size);
// 	}
// }

Prrand : Pchoose {
	*new { arg size, minVal = 0.0, maxVal = 1.0, repeats=1;
		var arr = Array.rand(size, minVal, maxVal);
		^super.newCopyArgs(arr, repeats, 0, size)
	}
}

// Cycles over a list of values, shifting them a number positions every cycle.
Protate : ListPattern {
	var <> shift;

	*new { arg list, shift = 1, repeats = inf;
		^super.newCopyArgs(list, repeats, shift);
	}

	embedInStream { arg inval;
		var item, stream;
		var localList = list;

		repeats.value(inval).do({ arg j;
			localList.size.do({ arg i;
				item = localList.wrapAt(i);
				inval = item.embedInStream(inval);
			});
			localList = localList.rotate(shift);
		});
		^inval;
	}
}

// Arvo Pärt's Tintinnabuli M-Voice
// Pmvoice {
// 	var <>note, <>scale, <>mode, <>tonic;
// 	*new { arg note=0, mode=1, scale=Pkey(\scale), tonic=0;
// 		^super.newCopyArgs(note, mode, scale, tonic)
// 	}
//
// 	storeArgs { ^[note, mode, scale, tonic] }
//
// 	embedInStream { arg inval;
// 		var noteStr = note.asStream;
// 		var modeStr = mode.asStream;
// 		var noteVal, modeVal;
// 		length.value(inval).do({
// 			hiVal = hiStr.next(inval);
// 			loVal = loStr.next(inval);
// 			if(hiVal.isNil or: { loVal.isNil }) { ^inval };
// 			inval = rrand(loVal, hiVal).yield;
// 		});
// 		^inval;
// 	}
// }

//
// Pmidi : Pbind {
// 	var <>midiout;
//
// 	*new { arg midiout, pairs;
// 		if (pairs.size.odd, { Error("Pbind should have even number of args.\n").throw; });
// 		^super.newCopyArgs(pairs).midiout_(midiout);
// 	}
//
// 	embedInStream { arg inval;
//
// 		^super.embedInStream(inval);
// 	}
// }
