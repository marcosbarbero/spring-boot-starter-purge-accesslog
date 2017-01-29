package com.marcosbarbero.boot.purge.accesslog.holder;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.web.ServerProperties.Undertow.Accesslog;

import static com.marcosbarbero.boot.purge.accesslog.holder.helper.TestUtils.TEMP_DIR;
import static com.marcosbarbero.boot.purge.accesslog.holder.helper.TestUtils.createOldAccessLogFiles;
import static com.marcosbarbero.boot.purge.accesslog.holder.helper.TestUtils.getCurrentAccessLogFileName;
import static com.marcosbarbero.boot.purge.accesslog.holder.helper.TestUtils.resetTestDirectory;
import static java.nio.file.Files.createFile;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.marcosbarbero.boot.purge.accesslog.properties.PurgeProperties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Matheus Góes
 */
@RunWith(JUnit4.class)
public class UndertowPurgeAccessLogHolderTest {

	private static final int FILES_AMOUNT = 10;
	private UndertowPurgeAccessLogHolder holderUnderTest;
	private PurgeProperties purgeProperties;
	private Accesslog accesslog;

	@Before
	public void setUp() throws Exception {
		resetTestDirectory();

		this.purgeProperties = new PurgeProperties();
		this.purgeProperties.setEnabled(true);
		this.purgeProperties.setExecutionInterval(2);
		this.purgeProperties.setExecutionIntervalUnit(SECONDS);
		this.purgeProperties.setMaxHistory(5);
		this.purgeProperties.setMaxHistoryUnit(SECONDS);

		this.accesslog = new Accesslog();
		this.accesslog.setPrefix("access_log");
		this.accesslog.setSuffix(".log");
		this.accesslog.setDir(new File(TEMP_DIR));

		this.holderUnderTest = new UndertowPurgeAccessLogHolder(this.purgeProperties,
				this.accesslog.getDir().toPath(), this.accesslog.getPrefix(),
				this.accesslog.getSuffix());

		createFile(Paths
				.get(TEMP_DIR + getCurrentAccessLogFileName(this.accesslog.getPrefix(),
						this.accesslog.getSuffix(), false)));

		createOldAccessLogFiles(FILES_AMOUNT, this.accesslog.getPrefix(),
				this.accesslog.getSuffix());
	}

	@Test
	public void customize() throws Exception {
		this.holderUnderTest.customize(null);
		final String[] fileNames = this.accesslog.getDir().list();
		assertNotNull(fileNames);
		assertEquals(FILES_AMOUNT + 1, fileNames.length);
	}

	@Test
	public void testCustomizeExecutingOnStartup() throws Exception {
		this.purgeProperties.setExecuteOnStartup(true);
		this.holderUnderTest.customize(null);

		String[] fileNames = this.accesslog.getDir().list();
		assertNotNull(fileNames);
		assertEquals(FILES_AMOUNT + 1, fileNames.length);

		TimeUnit.SECONDS.sleep(10);
		fileNames = this.accesslog.getDir().list();
		assertNotNull(fileNames);
		assertEquals(1, fileNames.length);

		createOldAccessLogFiles(FILES_AMOUNT, this.accesslog.getPrefix(),
				this.accesslog.getSuffix());

		TimeUnit.SECONDS.sleep(10);
		fileNames = this.accesslog.getDir().list();
		assertNotNull(fileNames);
		assertEquals(1, fileNames.length);
	}

}