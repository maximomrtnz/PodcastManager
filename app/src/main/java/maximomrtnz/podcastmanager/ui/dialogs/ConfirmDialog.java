package maximomrtnz.podcastmanager.ui.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by maximo on 07/08/16.
 */

public class ConfirmDialog extends DialogFragment{

    private ConfirmDialogListener mListener;

    public interface ConfirmDialogListener {
        void onConfirm();
        void onCancel();
    }

    public void setArgs(ConfirmDialogListener listener, String title, String message, String positiveButton, String negativeButton) {

        mListener = listener;

        Bundle args = new Bundle();

        args.putString("negativeButton", negativeButton);
        args.putString("positiveButton", positiveButton);
        args.putString("message", message);
        args.putString("title", title);
        setArguments(args);

    }


    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Resources res = getActivity().getResources();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String title = getArguments().getString("title");

        builder.setTitle(title);

        String positiveButton = getArguments().getString("positiveButton");

        String negativeButton = getArguments().getString("negativeButton");

        // Set confirmation message
        builder.setMessage(getArguments().getString("message"));

        // Set confirmation action
        builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onConfirm();
            }
        });

        // Set cancel action
        builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onCancel();
            }
        });
        return builder.create();
    }



}
