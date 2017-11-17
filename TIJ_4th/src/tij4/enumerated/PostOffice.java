package tij4.enumerated;

//: enumerated/PostOffice.java
// Modeling a post office.
import static net.mindview.util.Print.print;

import java.util.Iterator;

import net.mindview.util.Enums;

class Mail {
	// The NO's lower the probability of random selection:
	enum GeneralDelivery {
		YES, NO1, NO2, NO3, NO4, NO5
	}

	enum Scannability {
		UNSCANNABLE, YES1, YES2, YES3, YES4
	}

	enum Readability {
		ILLEGIBLE, YES1, YES2, YES3, YES4
	}

	enum Address {
		INCORRECT, OK1, OK2, OK3, OK4, OK5, OK6
	}

	enum ReturnAddress {
		MISSING, OK1, OK2, OK3, OK4, OK5
	}

	GeneralDelivery generalDelivery;
	Scannability scannability;
	Readability readability;
	Address address;
	ReturnAddress returnAddress;
	static long counter = 0;
	long id = counter++;

	public String toString() {
		return "Mail " + id;
	}

	public String details() {
		return toString() + ", General Delivery: " + generalDelivery
				+ ", Address Scanability: " + scannability
				+ ", Address Readability: " + readability
				+ ", Address Address: " + address + ", Return address: "
				+ returnAddress;
	}

	// Generate test Mail:
	public static Mail randomMail() {
		Mail m = new Mail();
		// Marvin: 这种用法就叫做 class literal
		m.generalDelivery = Enums.random(GeneralDelivery.class);
		m.scannability = Enums.random(Scannability.class);
		m.readability = Enums.random(Readability.class);
		m.address = Enums.random(Address.class);
		m.returnAddress = Enums.random(ReturnAddress.class);
		return m;
	}

	public static Iterable<Mail> generator(final int count) {
		return new Iterable<Mail>() {
			int n = count;

			public Iterator<Mail> iterator() {
				return new Iterator<Mail>() {
					public boolean hasNext() {
						return n-- > 0;
					}

					public Mail next() {
						return randomMail();
					}

					public void remove() { // Not implemented
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
}

public class PostOffice {
	enum MailHandler {
		GENERAL_DELIVERY {
			boolean handle(Mail m) {
				switch (m.generalDelivery) {
				case YES:
					print("Using general delivery for " + m);
					return true;
				default:
					return false;
				}
			}
		},
		MACHINE_SCAN {
			boolean handle(Mail m) {
				switch (m.scannability) {
				case UNSCANNABLE:
					return false;
				default:
					switch (m.address) {
					case INCORRECT:
						return false;
					default:
						print("Delivering " + m + " automatically");
						return true;
					}
				}
			}
		},
		VISUAL_INSPECTION {
			boolean handle(Mail m) {
				switch (m.readability) {
				case ILLEGIBLE:
					return false;
				default:
					switch (m.address) {
					case INCORRECT:
						return false;
					default:
						print("Delivering " + m + " normally");
						return true;
					}
				}
			}
		},
		RETURN_TO_SENDER {
			boolean handle(Mail m) {
				switch (m.returnAddress) {
				case MISSING:
					return false;
				default:
					print("Returning " + m + " to sender");
					return true;
				}
			}
		};

		abstract boolean handle(Mail m);

	}

	static void handle_a_mail_by_all_strategies(Mail m) {
		for (MailHandler oneHandler : MailHandler.values())
			if (oneHandler.handle(m)) {
				return;
			}
		print(m + " is a dead letter");
	}

	public static void main(String[] args) {
		Iterable<Mail> dddd = Mail.generator(10);
		for (Mail mail : dddd) {
			print(mail.details());
			handle_a_mail_by_all_strategies(mail);
			print("*****");
		}
	}
}
