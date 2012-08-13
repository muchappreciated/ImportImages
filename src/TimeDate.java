
public class TimeDate
{

   public TimeDate()
	{
        //Empty constructor - TimeDate used to hold header information
    }

	private String year;
	private String month;
	private String day;
	
	private String hour;
	private String minute;
	private String second;
	
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}

	public String getSecond() {
		return second;
	}

	public void setSecond(String second) {
		this.second = second;
	}
	
	public String getAll()
	{	
		return this.year + ":" + this.month + ":" + this.day + " " + this.hour + ":" + this.minute + ":" + this.second;
	}



	
	
}
