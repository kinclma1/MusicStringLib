package org.herac.tuxguitar.player.base;

import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;

public class MidiRepeatController {
	
	private TGSong song;
	private int count;
	private int index;
	private int lastIndex;	
	private boolean shouldPlay;	
	private boolean repeatOpen;
	private long repeatStart;
	private long repeatEnd;
	private long repeatMove;
	private int repeatStartIndex;
	private int repeatNumber;
	private int repeatAlternative;
	private int sHeader;
	private int eHeader;
	
	public MidiRepeatController(TGSong song, int sHeader , int eHeader){
		this.song = song;
		this.sHeader = sHeader;
		this.eHeader = eHeader;
        count = song.countMeasureHeaders();
        index = 0;
        lastIndex = -1;
        shouldPlay = true;
        repeatOpen = true;
        repeatAlternative = 0;
        repeatStart = TGDuration.QUARTER_TIME;
        repeatEnd = 0;
        repeatMove = 0;
        repeatStartIndex = 0;
        repeatNumber = 0;
	}
	
	public void process(){
		TGMeasureHeader header = song.getMeasureHeader(index);
		
		//Verifica si el compas esta dentro del rango.
		if( (sHeader != -1 && header.getNumber() < sHeader) || (eHeader != -1 && header.getNumber() > eHeader) ){
            shouldPlay = false;
            index++;
			return;
		}
		
		//Abro repeticion siempre para el primer compas.
		if( (sHeader != -1 && header.getNumber() == sHeader) || header.getNumber() == 1 ){
            repeatStartIndex = index;
            repeatStart = header.getStart();
            repeatOpen = true;
		}
		
		//Por defecto el compas deberia sonar
        shouldPlay = true;
		
		//En caso de existir una repeticion nueva,
		//guardo el indice de el compas donde empieza una repeticion
		if (header.isRepeatOpen()) {
            repeatStartIndex = index;
            repeatStart = header.getStart();
            repeatOpen = true;
			
			//Si es la primer vez que paso por este compas
			//Pongo numero de repeticion y final alternativo en cero
			if(index > lastIndex){
                repeatNumber = 0;
                repeatAlternative = 0;
			}
		}
		else{
			//verifico si hay un final alternativo abierto
			if(repeatAlternative == 0){
                repeatAlternative = header.getRepeatAlternative();
			}
			//Si estoy en un final alternativo.
			//el compas solo puede sonar si el numero de repeticion coincide con el numero de final alternativo.
			if (repeatOpen && (repeatAlternative > 0) && ((repeatAlternative & (1 << (repeatNumber))) == 0)){
                repeatMove -= header.getLength();
				if (header.getRepeatClose() >0){
                    repeatAlternative = 0;
				}
                shouldPlay = false;
                index++;
				return;
			}
		}
		
		//antes de ejecutar una posible repeticion
		//guardo el indice del ultimo compas tocado 
        lastIndex = Math.max(lastIndex, index);
		
		//si hay una repeticion la hago
		if (repeatOpen && header.getRepeatClose() > 0) {
			if (repeatNumber < header.getRepeatClose() || (repeatAlternative > 0)) {
                repeatEnd = header.getStart() + header.getLength();
                repeatMove += repeatEnd - repeatStart;
                index = repeatStartIndex - 1;
                repeatNumber++;
			} else{
                repeatStart = 0;
                repeatNumber = 0;
                repeatEnd = 0;
                repeatOpen = false;
			}
            repeatAlternative = 0;
		}

        index++;
	}
	
	public boolean finished(){
		return (index >= count);
	}
	
	public boolean shouldPlay(){
		return shouldPlay;
	}
	
	public int getIndex(){
		return index;
	}
	
	public long getRepeatMove(){
		return repeatMove;
	}
}
