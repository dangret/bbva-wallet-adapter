package com.bbva.wallet.xls.adapter.BbvaAdapter;

import com.bbva.wallet.xls.adapter.BbvaAdapter.adapter.BbvaWalletXmlAdapter;
import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.Record;
import com.bbva.wallet.xls.adapter.BbvaAdapter.service.EntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
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

	private void importPath(String... args) {
		switch (args[1]) {
			case "--bbva": importFromBbva(args[3], args[2]); break;
			case "--wallet": importFromWallet(args[2]); break;
			default: throw new IllegalArgumentException();
		}
	}

	private void importFromBbva(String xlsXFilePath, String type) {
		List<Record> entries = switch (type) {
            case "--cc" -> bbvaWalletXmlAdapter.importFromCreditCardBbva(new File(xlsXFilePath));
            case "--dc" -> bbvaWalletXmlAdapter.importFromDebitCardBbva(new File(xlsXFilePath));
            default -> null;
        };

        entryService.save(entries);
	}

	private void importFromWallet(String xlsXFilePath) {
		List<Record> entries = bbvaWalletXmlAdapter.importFromWallet(new File(xlsXFilePath));
		entryService.update(entries);
	}
}
