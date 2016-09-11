package utilityfunction.config;

import org.matsim.core.config.experimental.ReflectiveConfigGroup;


public class MPT_PragmaticLongDistanceDriverConfigGroup extends ReflectiveConfigGroup {
	
	public static final String GROUP_NAME = "MPT_PragmaticLongDistanceDriver";
		
	private Double MPTValue_Walk = null;
	
	private Double MPTValue_PT = null;
	
	private Double MPTValue_Car = null;
	
	private Double MPTValue_CS_Ride = null;
	
	private Double MPTValue_Train = null;

	
	public MPT_PragmaticLongDistanceDriverConfigGroup() {
		super(GROUP_NAME);
	}
	
	@StringGetter( "MPTValue_Walk" )
	public Double getMPTValue_Walk() {
		return this.MPTValue_Walk;
	}

	@StringSetter( "MPTValue_Walk" )
	public void setMPTValue_Walk(final Double MPTValue_Walk) {
		this.MPTValue_Walk = MPTValue_Walk;
	}

	@StringGetter( "MPTValue_PT" )
	public Double getMPTValue_PT() {
		return this.MPTValue_PT;
	}

	@StringSetter( "MPTValue_PT" )
	public void setMPTValue_PT(final Double MPTValue_PT) {
		this.MPTValue_PT = MPTValue_PT;
	}
	
	@StringGetter( "MPTValue_Car" )
	public Double getMPTValue_Car() {
		return this.MPTValue_Car;
	}

	@StringSetter( "MPTValue_Car" )
	public void setMPTValue_Car(final Double MPTValue_Car) {
		this.MPTValue_Car = MPTValue_Car;
	}
	
	@StringGetter( "MPTValue_CS_Ride" )
	public Double getMPTValue_CS_Ride() {
		return this.MPTValue_CS_Ride;
	}

	@StringSetter( "MPTValue_CS_Ride" )
	public void setMPTValue_CS_Ride(final Double MPTValue_CS_Ride) {
		this.MPTValue_CS_Ride = MPTValue_CS_Ride;
	}
	
	@StringGetter( "MPTValue_Train" )
	public Double getMPTValue_Train() {
		return this.MPTValue_Train;
	}

	@StringSetter( "MPTValue_Train" )
	public void setMPTValue_Train(final Double MPTValue_Train) {
		this.MPTValue_Train = MPTValue_Train;
	}
}