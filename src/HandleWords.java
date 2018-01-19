


public class HandleWords {

	public static void main(String[] args) {
		long StartTime = System.currentTimeMillis();

		Processor.process();

		long ProcessTime = System.currentTimeMillis() - StartTime;
		System.out.println("Process time：" + Double.parseDouble(String.valueOf(ProcessTime))/1000 + " sec");	

	}

}
