package User;

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

	@Override
	public boolean equals(Object obj) {
		User aux = (User) obj;
		return (aux.getID() == ID && obj instanceof User);
	}

	@Override
	public int hashCode() {
		return ID;
	}

	@Override
	public String toString() {
		return ip + " " + port + " " + ID;
	}

}
