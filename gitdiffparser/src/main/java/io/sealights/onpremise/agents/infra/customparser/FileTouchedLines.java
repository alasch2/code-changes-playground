package io.sealights.onpremise.agents.infra.customparser;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class FileTouchedLines {
	
	private List<TouchedLines> touchedList = new ArrayList<>();
	
	public void addTouchedLines(TouchedLines touchedLines) {
		touchedList.add(touchedLines);
	}
	
	@Data
	public static class TouchedLines {
		private RevisionRange oldRevision;
		private RevisionRange newRevision;
		
		public TouchedLines(int startOld, int startNew) {
			oldRevision = new RevisionRange(startOld);
			newRevision = new RevisionRange(startNew);
		}
		
		public void addOldTouchedLine(int lineNumber) {
			oldRevision.addTouched(lineNumber);
		}
		
		public void addNewTouchedLine(int lineNumber) {
			newRevision.addTouched(lineNumber);
		}
		
		@Override
		public String toString() {
			return String.format("oldRange:{%s}, newRange:{%s}", oldRevision, newRevision);
		}		
	}
	
	@Data
	public static class RevisionRange {
		private int start;
		private List<Integer> touched;
		
		public RevisionRange(int start) {
			this.start = start;
		}
		
		public RevisionRange(int start, List<Integer> touched) {
			this(start);
			this.touched = touched;
		}
		
		public void addTouched(int lineNumber) {
			if (touched == null) {
				touched = new ArrayList<>();
			}
			touched.add(lineNumber);
		}
		
		@Override
		public String toString() {
			return String.format("start:%s, touched:[%s]", start, touched);
		}
	}

}
