package Server;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = 7714213425076349573L;

	private String ip;
	private int port, ID;

	public User(String ip, int port, int ID) {
		this.ip = ip;
		this.port = port;
		this.ID = ID;
	}

	public String getEndereco() {
		return ip;
	}

	public int getPorto() {
		return port;
	}

	public int getID() {
		return ID;
	}

	public String toString() {
		return "Utilizador: " + ID + ", " + ip + ", " + port;
	}

}
