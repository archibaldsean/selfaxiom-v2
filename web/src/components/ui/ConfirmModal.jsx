import ModalShell from "./ModalShell";

export default function ConfirmModal({ title, message, confirmLabel = "Delete", onConfirm, onClose, busy }) {
  return (
    <ModalShell title={title} onClose={onClose}>
      <div className="modal-form">
        <p>{message}</p>
        <div className="inline-form-actions">
          <button className="ghost-button" type="button" onClick={onClose}>
            Cancel
          </button>
          <button className="primary-button" type="button" onClick={onConfirm} disabled={busy}>
            {busy ? "Working..." : confirmLabel}
          </button>
        </div>
      </div>
    </ModalShell>
  );
}
