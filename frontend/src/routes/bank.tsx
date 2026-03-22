import { useMutation } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import { type ChangeEvent, useState } from "react";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

export const Route = createFileRoute("/bank")({
    component: Bank,
});

interface BankRequest {
    countryCode: string;
    bankCode: string;
    name: string;
    accountAlgo?: string;
}

function Bank() {
    const [countryCode, setCountryCode] = useState("");
    const [bankCode, setBankCode] = useState("");
    const [name, setName] = useState("");
    const [bic, setBic] = useState("");
    const [accountAlgo, setAccountAlgo] = useState("");

    const mutation = useMutation({
        mutationFn: async (bank: BankRequest) => {
            const res = await fetch("/api/v1/bank/", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(bank),
            });
            if (!res.ok) {
                throw new Error(`Request failed: ${res.status}`);
            }
            return res.json();
        },
    });

    const isValid = countryCode.length === 2 && bankCode.trim() !== "" && name.trim() !== "";

    const handleSubmit = (e: ChangeEvent) => {
        e.preventDefault();
        if (!isValid) return;
        mutation.mutate({
            countryCode,
            bankCode,
            name,
            ...(accountAlgo ? { accountAlgo } : {}),
        });
    };

    return (
        <div className="mx-auto max-w-md p-6">
            <h1 className="mb-6 text-2xl font-bold text-foreground!">Create Bank</h1>
            <form onSubmit={handleSubmit} className="space-y-4">
                <div className="space-y-1.5">
                    <Label htmlFor="countryCode">Country Code *</Label>
                    <Input
                        id="countryCode"
                        value={countryCode}
                        onChange={(e) =>
                            setCountryCode(e.target.value.replace(/[^a-zA-Z]/g, "").toUpperCase().slice(0, 2))
                        }
                        maxLength={2}
                        placeholder="DE"
                        required
                    />
                </div>

                <div className="space-y-1.5">
                    <Label htmlFor="bankCode">Bank Code with Branch Code *</Label>
                    <Input
                        id="bankCode"
                        value={bankCode}
                        onChange={(e) => setBankCode(e.target.value.replace(/[^a-zA-Z0-9\s]/g, ""))}
                        placeholder="10000000"
                        required
                    />
                </div>

                <div className="space-y-1.5">
                    <Label htmlFor="name">Bank Name *</Label>
                    <Input
                        id="name"
                        value={name}
                        onChange={(e) => setName(e.target.value.replace(/[^a-zA-Z0-9\s-]/g, ""))}
                        placeholder="Deutsche Bundesbank"
                        required
                    />
                </div>

                <div className="space-y-1.5">
                    <Label htmlFor="bic">BIC</Label>
                    <Input
                        id="bic"
                        value={bic}
                        onChange={(e) => setBic(e.target.value)}
                        placeholder="MARKDEFF"
                    />
                </div>

                <div className="space-y-1.5">
                    <Label htmlFor="accountAlgo">Account Algorithm</Label>
                    <Input
                        id="accountAlgo"
                        value={accountAlgo}
                        onChange={(e) => setAccountAlgo(e.target.value)}
                        placeholder="09"
                    />
                </div>

                <button
                    type="submit"
                    disabled={!isValid || mutation.isPending}
                    title={!isValid ? "Please fill in all required fields" : undefined}
                    className="w-full rounded-lg bg-primary px-4 py-2 text-sm font-medium text-primary-foreground transition-colors hover:bg-primary/90 disabled:opacity-50"
                >
                    {mutation.isPending ? "Submitting…" : "Create Bank"}
                </button>

                {mutation.isSuccess && (
                    <p className="text-sm text-green-600">Bank created successfully.</p>
                )}
                {mutation.isError && (
                    <p className="text-sm text-destructive">
                        Error: {mutation.error.message}
                    </p>
                )}
            </form>
        </div>
    );
}
