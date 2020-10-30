import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Scanner;

public class Files {
	private PrintWriter tmpdir;
	private PrintWriter tabsim;
	private PrintWriter tmpFile;

	private Path rutasim;
	private Path rutatmp;
	private Path tmp;

	public Files(Path ruta) throws Exception{
		this.rutatmp = java.nio.file.Paths.get(ruta.toAbsolutePath().getParent().toString() + "\\tmp.txt");
		this.rutasim = java.nio.file.Paths.get(ruta.toAbsolutePath().getParent().toString() + "\\tabsim.txt");

		this.tmpdir = new PrintWriter(ruta.toAbsolutePath().getParent().toString() + "\\tmp.txt");
		this.tabsim = new PrintWriter(ruta.toAbsolutePath().getParent().toString() + "\\tabsim.txt");
	}

	public boolean verifyLabel(Instruccion instr, Bases conv, ContLoc contloc, MatchesElement matcher) {
		try(Scanner file = new Scanner(this.rutasim)) {
			while(file.hasNextLine()) {
				String data = file.nextLine().trim();
				String [] line = data.split("\t");
				
				String tmp = line[1];
				String value = line[2];

				if(tmp.equals(instr.getLabel()) && !value.equals(instr.getContloc())) {
					return false;
				}
			}
		} catch (Exception ex) {
			System.out.println("Error al leer la tabla de simbolos: " + ex.getMessage());
		}
		return true;
	}

	public boolean repeated(Instruccion instr) {
		try(Scanner file = new Scanner(this.rutatmp)) {
			while(file.hasNextLine()) {
				String data = file.nextLine().trim();
				String [] line = data.split("\t");
				
				String tmp = line[3];
				String value = line[2];
				if(tmp.equals(instr.getCodop()) && value.equals(instr.getLabel())) {
					return false;
				}
			}
		} catch (Exception ex) {
			System.out.println("Error al leer la tabla de simbolos: " + ex.getMessage());
		}
		return true;
	}

	public Path getRSim() {
		return this.rutasim;
	}

	public Path getRTmp() {
		return this.rutatmp;
	}

	public void writeTmp(Instruccion instr, String type) {
		String label = instr.getLabel();
		String op = instr.getOp();
		if(instr.getLabel().equals("")) {
			label = "NULL";
		}
		if(instr.getOp().equals("")) {
			op = "NULL";
		}
		this.tmpdir.println(type + "\t" + instr.getContloc() + "\t" + label + "\t" + instr.getCodop() + "\t" + op);
	}

	public void writeTabSim(Instruccion instr, String type, String value) {
		this.tabsim.println(type + "\t" + instr.getLabel() + "\t" + value);
	}

	public void writeTmpFile(String line, String codMaq) {
		this.tmpFile.println(line + (codMaq.equals("")? "" : "\t" + codMaq));
	}

	public void tmpFile() throws java.io.IOException, java.io.FileNotFoundException {
        this.tmp = java.nio.file.Files.createTempFile("tmpfile", ".txt");
        this.tmpFile = new PrintWriter(tmp.toString());
	}

	public void endFase() throws java.io.IOException {
		this.tmpFile.close();
		java.nio.file.Files.copy(this.tmp, this.rutatmp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
	}

	public String getSymb(String label) throws RuntimeErrors {
		try(Scanner file = new Scanner(this.rutasim)) {
			while(file.hasNextLine()) {
				String data = file.nextLine().trim();
				String [] line = data.split("\t");
				
				if(label.equals(line[1])) {
					return line[2];
				}
			}
		} catch (Exception ex) {
			System.out.println("Error al leer la tabla de simbolos: " + ex.getMessage());
		}
		throw new RuntimeErrors(4);
	}

	public void flushTmp() {
		this.tmpFile.flush();
	}

	public void flush() {
		this.tmpdir.flush();
		this.tabsim.flush();
	}

	public void closeTmp() {
		this.tmpdir.close();
	}

	public void closeTabSim() {
		this.tabsim.close();
	}

	public void closeAll() {
		this.tmpdir.close();
		this.tabsim.close();
	}
}
