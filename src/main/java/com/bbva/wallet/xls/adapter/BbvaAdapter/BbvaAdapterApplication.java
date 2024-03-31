package com.bbva.wallet.xls.adapter.BbvaAdapter;

import com.bbva.wallet.xls.adapter.BbvaAdapter.adapter.impl.BbvaWalletXmlAdapter;
import com.bbva.wallet.xls.adapter.BbvaAdapter.service.EntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class BbvaAdapterApplication implements CommandLineRunner {

	EntryService entryService;
	BbvaWalletXmlAdapter bbvaWalletXmlAdapter;

	public static void main(String[] args) {
		SpringApplication.run(BbvaAdapterApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//List<Entry> entries = bbvaXmlReader.read(new File(args[0]));
		//entryService.save(entries);
	}
}
