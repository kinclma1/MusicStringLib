
package cz.cvut.fel.kinclma1;

import cz.cvut.fel.kinclma1.io.FileImporter;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;

/**
 *
 * @author void
 */
public class MusicStringSong {

    private class TrackCreator implements Callable<MusicStringTrack> {

        private TGTrack track;
        private String strTrack;

        public TrackCreator(TGTrack track) {
            this.track = track;
        }

        public TrackCreator(String strTrack) {
            this.strTrack = strTrack;
        }

        @Override
        public MusicStringTrack call() throws Exception {
            return track != null ? new MusicStringTrack(track) : new MusicStringTrack(strTrack);
        }
    }

    private class TrackExporter implements Callable<TGTrack> {
        private MusicStringTrack track;
        private TGFactory factory;

        public TrackExporter(MusicStringTrack track, TGFactory factory) {
            this.track = track;
            this.factory = factory;
        }

        @Override
        public TGTrack call() throws Exception {
            return track.toTGTrack(factory);
        }
    }

    private Map<String, MusicStringTrack> tracks;

    /**
     * Creates a MusicString from a given file, if the file format is supported
     * @param file Input file
     */
    public MusicStringSong(File file) {
        this(new FileImporter().importFile(file));
    }

    /**
     * Creates MusicString from a string representation of music string
     * @param musicString a string representation of a music string
     */
    public MusicStringSong(String musicString) {
        this();

        //http://stackoverflow.com/questions/2206378/how-to-split-a-string-but-also-keep-the-delimiters
        String[] strTracks = musicString.split("\\s(?=V[0-9])");

        List<Callable<MusicStringTrack>> tcs = new ArrayList<Callable<MusicStringTrack>>(strTracks.length);
        for (String track : strTracks) {
            tcs.add(new TrackCreator(track));
        }

        create(tcs);
    }

    /**
     * Creates a MusicString from a given TGSong object
     * @param song Input TGSong object
     */
    public MusicStringSong(TGSong song) {
        this();

        Iterator<TGTrack> tgTracks = song.getTracks();

        List<Callable<MusicStringTrack>> tcs = new ArrayList<Callable<MusicStringTrack>>(song.countTracks());
        while (tgTracks.hasNext()) {
            tcs.add(new TrackCreator(tgTracks.next()));
        }

        create(tcs);
    }

    private MusicStringSong() {
        tracks = new HashMap<String, MusicStringTrack>();
    }

    /**
     * Returns a music String containing all tracks of the song
     * @return all tracks in a single String
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String key : getTrackIds()) {
            sb.append(tracks.get(key));
        }
        return sb.toString();
    }

    public TGSong toTGSong() {
        TGFactory factory = new TGFactory();
        TGSong song = factory.newSong();

        Collection<Callable<TGTrack>> tcs = new ArrayList<Callable<TGTrack>>(tracks.size());
        for (String trackId : getTrackIds()) {
            tcs.add(new TrackExporter(getTrack(trackId), factory));
        }

        List<TGTrack> tgTracks = Parallellization.executeSingleBatch(tcs);

        int i = 0;
        for (TGTrack tgTrack : tgTracks) {
            tgTrack.setNumber(i++);
            song.addTrack(tgTrack);
        }

        Iterator<TGMeasure> it = song.getTrack(0).getMeasures();
        while (it.hasNext()) {
            song.addMeasureHeader(it.next().getHeader());
        }

        return song;
    }

    /**
     * Returns IDs of all tracks, used to get a single track
     * @return all track IDs
     */
    public List<String> getTrackIds() {
        List<String> list = new ArrayList<String>(tracks.keySet());
        Collections.sort(list);
        return list;
    }
    
    /**
     * Returns the track with the given ID
     * @param id ID of the wanted track, list of all IDs can be obtained with
     *              getTrackIds method
     * @return music String representing a single track
     */
    public MusicStringTrack getTrack(String id) {
        return tracks.get(id);
    }

    public void removeTrack(String id) {
        tracks.remove(id);
    }

    public MusicStringTrack getPossibleNotes() {
        System.out.println(new HarmonyDetector(this).detectHarmony());
        return null;
    }

    private void create(List<Callable<MusicStringTrack>> tcs) {
        List<MusicStringTrack> msTracks = Parallellization.executeSingleBatch(tcs);
        for (MusicStringTrack msTrack : msTracks) {
            tracks.put(msTrack.getId(), msTrack);
        }
    }
}
