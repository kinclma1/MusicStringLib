
package cz.cvut.fel.kinclma1;

import cz.cvut.fel.kinclma1.io.FileExporter;
import cz.cvut.fel.kinclma1.io.FileImporter;
import org.herac.tuxguitar.io.base.*;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.song.models.TGTrack;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

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

    private Map<String, MusicStringTrack> tracks = new HashMap<String, MusicStringTrack>();

    public static MusicStringSong create(String filename) throws IOException, TGFileFormatException {
        if (filename.substring(filename.lastIndexOf('.')).contains("musicstring")) {
            StringBuilder sb = new StringBuilder();
            BufferedReader in = new BufferedReader(new FileReader(filename));
            try {
                String line = in.readLine();
                while (line != null) {
                    sb.append(line);
                    line = in.readLine();
                }
            } finally {
                in.close();
            }
            return new MusicStringSong(sb.toString());
        } else {
            return new MusicStringSong(new FileImporter().importFile(filename));
        }
    }

    /**
     * Creates MusicString from a string representation of music string
     * @param musicString a string representation of a music string
     */
    public MusicStringSong(String musicString) {

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
        Iterator<TGTrack> tgTracks = song.getTracks();

        List<Callable<MusicStringTrack>> tcs = new ArrayList<Callable<MusicStringTrack>>(song.countTracks());
        while (tgTracks.hasNext()) {
            tcs.add(new TrackCreator(tgTracks.next()));
        }

        try {
            create(tcs);
        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
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

    public static TGFileFormat[] getInputFormats() {
        ArrayList<TGFileFormat> fmts = new ArrayList<TGFileFormat>();
        TGFileFormatManager formatManager = TGFileFormatManager.instance();
        List<TGFileFormat> is = formatManager.getInputFormats();
        fmts.addAll(is);
        Iterator<TGLocalFileImporter> imp = formatManager.getImporters();
        while (imp.hasNext()) {
            fmts.add(imp.next().getFileFormat());
        }
        fmts.add(new TGFileFormat("JFugue 4 MusicString", "*.musicstring"));
        return fmts.toArray(new TGFileFormat[fmts.size()]);
    }

    public static TGFileFormat[] getExportFormats() {
        ArrayList<TGFileFormat> fmts = new ArrayList<TGFileFormat>();
        TGFileFormatManager formatManager = TGFileFormatManager.instance();
        List<TGFileFormat> os = formatManager.getOutputFormats();
        fmts.addAll(os);
        Iterator<TGLocalFileExporter> exp = formatManager.getExporters();
        while (exp.hasNext()) {
            fmts.add(exp.next().getFileFormat());
        }
        fmts.add(new TGFileFormat("JFugue 4 MusicString", "*.musicstring"));
        return fmts.toArray(new TGFileFormat[fmts.size()]);
    }

    public void export(String filename) throws IOException, TGFileFormatException {
        try {
            if (filename.substring(filename.lastIndexOf('.')).contains("musicstring")) {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename));
                try {
                    out.write(toString().getBytes());
                } finally {
                    out.flush();
                    out.close();
                }
            } else {
                new FileExporter().exportSong(toTGSong(), filename);
            }
        } catch (FileNotFoundException e) {
            new File(filename).delete();
            throw e;
        } catch (TGFileFormatException e) {
            new File(filename).delete();
            throw e;
        } catch (IOException e) {
            new File(filename).delete();
            throw e;
        }
    }

    /**
     * Returns a TGSong object, a tuxGuitar internal representation of a song, created from this song
     * @return tuxGuitar internal representation of a song, created from this song
     */
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

    /**
     * Removes the track with the given ID from the song
     * @param id ID of the track to be removed, list of all IDs can be obtained with
     *              getTrackIds method
     */
    public void removeTrack(String id) {
        tracks.remove(id);
    }

    /**
     * Returns a track containing all notes that can surely be played in any additional track
     * @return track containing all notes that can surely be played in any additional track
     */
    public FlatTrack getPossibleNotes(InstrumentTones.Instruments toneFilter) {
        //todo not musicstringtrack - rather flattrack or string
        return new HarmonyDetector(this).detectHarmony(toneFilter);
    }

    private void create(List<Callable<MusicStringTrack>> tcs) {
        List<MusicStringTrack> msTracks;
        msTracks = Parallellization.executeSingleBatch(tcs);
        for (MusicStringTrack msTrack : msTracks) {
            tracks.put(msTrack.getId(), msTrack);
        }
    }
}
