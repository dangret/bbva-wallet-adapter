package com.bbva.wallet.xls.adapter.BbvaAdapter;

import com.bbva.wallet.xls.adapter.BbvaAdapter.adapter.BbvaWalletXmlAdapter;
import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.Record;
import com.bbva.wallet.xls.adapter.BbvaAdapter.service.EntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import javax.naming.directory.InvalidAttributeValueException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
@Component
public class BbvaAdapterApplication implements CommandLineRunner {

	private final EntryService entryService;
	private final BbvaWalletXmlAdapter bbvaWalletXmlAdapter;

	public static void main(String[] args) {
		SpringApplication.run(BbvaAdapterApplication.class, args);
	}

	/***
	 * Usage:
	 *  --import
	 *      --bbva
	 *      	--cc
	 *          --dc [filepath]
	 *      --wallet [filepath]
	 *  --export
	 *  	--account [account_name]
	 *  example:
	 *  	--import --bbva data.xlsx
	 *  	--export --account "Daniel TDD"
	 * @param args
	 * @throws Exception
	 */
	@Override
	public void run(String... args) throws Exception {
		if (args.length < 1) {
			System.out.println("No args, exiting");
			return;
		}
		switch (args[0]){
			case "--import":
				importPath(args);
				break;
			case "--export": exportPath(args); break;
			default: throw new IllegalArgumentException();
		}
	}

	private void exportPath(String[] args) throws IOException {
		if (args.length > 2) {
			File newFile = bbvaWalletXmlAdapter.exportToWallet(args[2]);
			if (newFile == null) {
				System.out.println("Nothing new to export");
				return;
			}
			System.out.printf("File %s created successfully%n", newFile.getPath());
		} else {
			bbvaWalletXmlAdapter.exportToWallet();
		}

	}

	private void importPath(String... args) throws InvalidAttributeValueException {
		switch (args[1]) {
			case "--bbva": importFromBbva(args); break;
			case "--wallet": importFromWallet(args[2]); break;
			default: throw new IllegalArgumentException();
		}
	}

	private void importFromBbva(String... args) throws InvalidAttributeValueException {
		String type = args[2];
		switch (type) {
					case "--all": bbvaWalletXmlAdapter.importToBbvaAll(); break;
					case "--cc": entryService.save(bbvaWalletXmlAdapter.importFromCreditCardBbva(new File(args[3])));
					break;
					case "--dc": entryService.save(bbvaWalletXmlAdapter.importFromDebitCardBbva(new File(args[3])));
					break;
        }
	}

	private void importFromWallet(String xlsXFilePath) {
		List<Record> entries = bbvaWalletXmlAdapter.importFromWallet(new File(xlsXFilePath));
		entryService.update(entries);
	}
}
