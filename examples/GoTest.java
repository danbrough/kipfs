

public class GoTest {
	public static void main(String args[]){
		System.out.println("Running go test");
		System.loadLibrary("kipfs");
		System.out.println("kipfs loaded");
		System.out.println("message: " + golib.getMessage());
		System.out.println("DAG: " + golib.dagCID("\"Hello World\""));
	}
}
