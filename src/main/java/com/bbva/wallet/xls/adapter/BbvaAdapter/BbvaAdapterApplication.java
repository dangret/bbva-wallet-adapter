package com.bbva.wallet.xls.adapter.BbvaAdapter;

import com.bbva.wallet.xls.adapter.BbvaAdapter.adapter.impl.BbvaWalletXmlAdapter;
import com.bbva.wallet.xls.adapter.BbvaAdapter.dto.Entry;
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
	 *      --bbva [filepath]
	 *      --wallet [filepath]
	 *  --export
	 *  	--account [account_name] --filename [filename]
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
		File newFile = bbvaWalletXmlAdapter.exportToWallet(args[2]);
		System.out.printf("File %s created successfully%n", newFile.getPath());
	}

	private void importPath(String... args) {
		String xlsxFilePath = args[2];
		switch (args[1]) {
			case "--bbva": importFromBbva(xlsxFilePath); break;
			case "--wallet": importFromWallet(xlsxFilePath); break;
			default: throw new IllegalArgumentException();
		}
	}

	private void importFromBbva(String xlsXFilePath) {
		List<Entry> entries = bbvaWalletXmlAdapter.importFromBbva(new File(xlsXFilePath));
		entryService.save(entries);
	}

	private void importFromWallet(String xlsXFilePath) {
		List<Entry> entries = bbvaWalletXmlAdapter.importFromWallet(new File(xlsXFilePath));
		entryService.update(entries);
	}
}
