package io.jamesclonk.turbojira;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import io.jamesclonk.turbojira.jira.Client;
import io.jamesclonk.turbojira.jira.Issue;
import io.jamesclonk.turbojira.jira.UpdateIssue;

public class EditIssueActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
    }

    private void setupView() {
        setContentView(R.layout.activity_edit_issue);

        final Issue issue = (Issue) getIntent().getSerializableExtra("JIRA_ISSUE");

        final Button button = (Button) findViewById(R.id.item_update);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateIssue(issue);
            }
        });

        TextView textView = (TextView) this.findViewById(R.id.item_key);
        textView.setText(issue.key);
        textView = (TextView) this.findViewById(R.id.item_status);
        textView.setText(issue.fields.status.name);

        textView = (TextView) this.findViewById(R.id.item_description);
        textView.setText(issue.fields.description);
        textView.addTextChangedListener(new InputValidator(textView) {
            @Override
            public void validate(TextView textView, String text) {
                issue.fields.description = text;
            }
        });

        textView = (TextView) this.findViewById(R.id.item_summary);
        textView.setText(issue.fields.summary);
        textView.addTextChangedListener(new InputValidator(textView) {
            @Override
            public void validate(TextView textView, String text) {
                if (text.isEmpty() || text.length() < 5) {
                    textView.setError("Please set a summary!");
                    button.setEnabled(false);
                } else {
                    issue.fields.summary = text;
                    button.setEnabled(true);
                }
            }
        });

        Spinner spinner = (Spinner) this.findViewById(R.id.item_type_spinner);
        final ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.jira_item_types, R.layout.item_spinner);
        typeAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner.setAdapter(typeAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                issue.fields.issuetype.name = typeAdapter.getItem(position).toString();
                updateItemIssueType(issue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        List<String> types = Arrays.asList(getResources().getStringArray(R.array.jira_item_types));
        spinner.setSelection(types.indexOf(issue.fields.issuetype.name));

        spinner = (Spinner) this.findViewById(R.id.item_priority_spinner);
        final ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(this, R.array.jira_item_priorities, R.layout.item_spinner);
        priorityAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner.setAdapter(priorityAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                issue.fields.priority.name = priorityAdapter.getItem(position).toString();
                updateItemPriority(issue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        List<String> priorities = Arrays.asList(getResources().getStringArray(R.array.jira_item_priorities));
        spinner.setSelection(priorities.indexOf(issue.fields.priority.name));

        updateItemIssueType(issue);
        updateItemPriority(issue);
    }

    private void updateItemIssueType(Issue issue) {
        ImageView itemType = (ImageView) this.findViewById(R.id.item_type);
        issue.UpdateTypeImageView(itemType);
    }

    private void updateItemPriority(Issue issue) {
        ImageView itemPriority = (ImageView) this.findViewById(R.id.item_priority);
        issue.UpdatePriorityImageView(itemPriority);
    }

    private void updateIssue(final Issue issue) {
        final Client client = new Client();

        final UpdateIssue updateIssue = new UpdateIssue(issue);
        client.updateIssue(updateIssue);

        finish();
        ActivityHolder.getItemListActivity().updateItemList();
    }
}
