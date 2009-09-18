package com.roiding.rterm;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.roiding.rterm.bean.Host;
import com.roiding.rterm.util.DBUtils;

public class EditHostActivity extends Activity {

	private Host h;
	private EditText name;
	private EditText host;
	private Spinner protocal;
	private EditText port;
	private Spinner encoding;
	private EditText user;
	private EditText pass;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_edithost);

		name = (EditText) findViewById(R.id.edithost_name);
		host = (EditText) findViewById(R.id.edithost_host);
		protocal = (Spinner) findViewById(R.id.edithost_protocal);
		port = (EditText) findViewById(R.id.edithost_port);
		encoding = (Spinner) findViewById(R.id.edithost_encoding);
		user = (EditText) findViewById(R.id.edithost_user);
		pass = (EditText) findViewById(R.id.edithost_pass);

		ArrayAdapter<CharSequence> adapterForProtocal = ArrayAdapter
				.createFromResource(this, R.array.protocals,
						android.R.layout.simple_spinner_item);
		adapterForProtocal
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		protocal.setAdapter(adapterForProtocal);

		ArrayAdapter<CharSequence> adapterForEncoding = ArrayAdapter
				.createFromResource(this, R.array.encodings,
						android.R.layout.simple_spinner_item);
		adapterForEncoding
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		encoding.setAdapter(adapterForEncoding);

		// if h is null, then create a new Host, otherwise, update a exist Host
		h = (Host) getIntent().getSerializableExtra("host");
		if (h != null) {
			name.setText(h.getName());
			host.setText(h.getHost());
			port.setText(String.valueOf(h.getPort()));
			if ("telnet".equalsIgnoreCase(h.getProtocal()))
				protocal.setSelection(0, true);
			else if ("ssh".equalsIgnoreCase(h.getProtocal()))
				protocal.setSelection(1, true);

			if ("GBK".equalsIgnoreCase(h.getEncoding()))
				encoding.setSelection(0, true);
			else if ("Big5".equalsIgnoreCase(h.getEncoding()))
				encoding.setSelection(1, true);

			user.setText(h.getUser());
			pass.setText(h.getPass());
		} else {
			port.setText("23");
		}

	}

	@Override
	public void onPause() {
		super.onPause();

		DBUtils dbUtils = new DBUtils(this);

		String hostName = name.getText().toString();
		String hostHost = host.getText().toString();
		String hostProtocal = protocal.getSelectedItem().toString();
		String hostPort = port.getText().toString();
		String hostEncoding = encoding.getSelectedItem().toString();
		String hostUser = user.getText().toString();
		String hostPass = pass.getText().toString();

		if (h != null) {
			h.setName(hostName);
			h.setHost(hostHost);
			h.setProtocal(hostProtocal);
			h.setEncoding(hostEncoding);
			h.setUser(hostUser);
			h.setPass(hostPass);

			try {
				h.setPort(Integer.parseInt(hostPort));
			} catch (Exception e) {
				if (hostProtocal.equalsIgnoreCase("telnet"))
					h.setPort(23);
				else if (hostProtocal.equalsIgnoreCase("ssh"))
					h.setPort(22);
			}

			dbUtils.updateHost(h);

		} else if (!("").equals(hostName)) {
			h = new Host();
			h.setName(hostName);
			h.setHost(hostHost);
			h.setProtocal(hostProtocal);
			h.setEncoding(hostEncoding);
			h.setUser(hostUser);
			h.setPass(hostPass);

			if (hostProtocal.equalsIgnoreCase("telnet"))
				h.setPort(23);
			else if (hostProtocal.equalsIgnoreCase("ssh"))
				h.setPort(22);
			dbUtils.saveHost(h);
		}
		dbUtils.close();
	}
}
