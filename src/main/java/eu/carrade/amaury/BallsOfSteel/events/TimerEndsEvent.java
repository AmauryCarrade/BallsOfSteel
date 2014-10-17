package eu.carrade.amaury.BallsOfSteel.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.carrade.amaury.BallsOfSteel.BoSTimer;

/**
 * This event is fired when a timer ends.
 * <p>
 * It is fired before all the values of the timer are reset.
 * 
 * @author Amaury Carrade
 */
public final class TimerEndsEvent extends Event {
    
	private static final HandlerList handlers = new HandlerList();
    
	private BoSTimer timer;
    private Boolean timerWasUp = false;
    private Boolean restart = false;
    
    
    public TimerEndsEvent(BoSTimer timer, Boolean timerUp) {
       this.timer = timer;
       
       this.timerWasUp = timerUp;
    }
    
    /**
     * Returns the timer.
     * 
     * @return the timer.
     */
    public BoSTimer getTimer() {
    	return timer;
    }
    
    /**
     * Returns true if the timer was stopped because it was up.
     * 
     * @return true if the timer was stopped because it was up.
     */
    public boolean wasTimerUp() {
    	return timerWasUp;
    }
    
    /**
     * If true, the timer will be restarted.
     * 
     * @param restart true if the timer needs to be restarted.
     */
    public void setRestart(boolean restart) {
    	this.restart = restart;
    }
    
    /**
     * Return true if the timer will be restarted.
     * 
     * @param restart true if the timer will be restarted.
     * @return 
     */
    public boolean getRestart() {
    	return this.restart;
    }
    
    
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}