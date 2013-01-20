package cz.cvut.fel.kinclma1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 19.1.13
 * Time: 22:09
 * To change this template use File | Settings | File Templates.
 */
class HarmonyDetector {

    private MusicStringSong song;
    private MusicStringTrack[] toneTracks;
    private Duration shortestNote;
    private int shortestValue;
    private int trackCount;
    private LinkedList<String>[] newTracks;
    private StringBuilder trackString;

    HarmonyDetector(MusicStringSong song) {
        this.song = song;
        this.toneTracks = getToneTracks();
        this.shortestNote = getShortestNote();
        this.shortestValue = shortestNote.toInteger();
        this.trackCount = toneTracks.length;
        trackString = new StringBuilder();
        this.newTracks = new LinkedList[trackCount];
        for (int i = 0; i < trackCount; i ++) {
            newTracks[i] = new LinkedList<String>();
        }
    }

    private MusicStringTrack[] getToneTracks() {
        ArrayList<MusicStringTrack> toneTracks = new ArrayList<MusicStringTrack>(song.getTrackIds().size());
        for (String trackId : song.getTrackIds()) {
            if (!trackId.contains("V9")) {
                toneTracks.add(song.getTrack(trackId));
            }
        }
        return toneTracks.toArray(new MusicStringTrack[toneTracks.size()]);
    }

    private Duration getShortestNote() {
        Duration shortest = Duration.WHOLE;
        MusicStringTrack track;
        for (int i = 0; i < toneTracks.length; i ++) {
            track = toneTracks[i];
            Duration trackShortest = track.getShortestNote();
            if (trackShortest.toInteger() > shortest.toInteger()) {
                shortest = trackShortest;
            }
        }
        return shortest;
    }

    MusicStringTrack detectHarmony() {

        Iterator<MusicStringMeasure>[] measureIterators = new Iterator[trackCount];
        for (int i = 0; i < trackCount; i ++) {
            measureIterators[i] = toneTracks[i].getMeasures().iterator();
        }

        //iterate over measures
        while (measureIterators[0].hasNext()){
            processMeasureThroughTracks(measureIterators);
        }

        //todo merge newTracks to stringbuilder
        //todo fuck arrays do one by one parallellized
        //todo create class musicstringtracknooctaves
        //todo in musicstringtrack setdefaultoctave if none
        //todo instrument defined notes -- not necessary

        return null;

    }

    private void processMeasureThroughTracks(Iterator<MusicStringMeasure>[] measureIterators) {
        MusicStringMeasure[] measures = new MusicStringMeasure[trackCount];
        Iterator<MusicStringBeat> beats;
        int durationCoef = 0;

        //iterate over same measures on different tracks
        for (int trackNum = 0; trackNum < trackCount; trackNum ++) {
            measures[trackNum] = measureIterators[trackNum].next();
            beats = measures[trackNum].getBeats().iterator();

            //while some remaining short notes to write
            while (beats.hasNext()) {
                MusicStringBeat beat = beats.next();
                while (durationCoef > 0) {
                    //todo to non octaved, non duration string
                    newTracks[trackNum].add(beat.toString());
                }
            }
        }
    }
}
