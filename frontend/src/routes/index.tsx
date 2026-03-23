import { useQuery } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import {
    AlertTriangle,
    CheckCircle2,
    Loader2,
    XCircle,
} from "lucide-react";
import { useState } from "react";
import { Input } from "@/components/ui/input";

type AccountValidationResult = "VALID" | "INVALID" | "NOT_IMPLEMENTED";

interface IbanValidationResponse {
    iban: string | null;
    patternValid: boolean;
    bankName: string | null;
    accountNumberValidation: AccountValidationResult | null;
    error: string | null;
}

function cleanIban(raw: string): string {
    return raw.replace(/[\s\-_]/g, "").toUpperCase();
}

async function validateIban(iban: string): Promise<IbanValidationResponse> {
    const res = await fetch(
        `/api/v1/iban/validation?iban=${encodeURIComponent(iban)}`,
    );
    if (res.ok || res.status === 400) {
        return res.json();
    }
    throw new Error("Validation request failed");
}

export const Route = createFileRoute("/")({
    component: Index,
});

function Index() {
    const [rawInput, setRawInput] = useState("");
    const cleaned = cleanIban(rawInput);

    const { data, isLoading, isError } = useQuery({
        queryKey: ["iban-validation", cleaned],
        queryFn: () => validateIban(cleaned),
        enabled: cleaned.length > 14 && cleaned.length < 35,
    });

    return (
        <div className="mx-auto max-w-lg space-y-6">
            <Input
                id="iban-input"
                type="text"
                placeholder="IBAN e.g. DE89 3704 0044 0532 0130 00"
                value={rawInput}
                onChange={(e) => {
                    const filtered = e.target.value.replace(/[^a-zA-Z0-9\s\-_]/g, "");
                    setRawInput(filtered.toUpperCase());
                }}
                className="h-12 text-lg tracking-wider font-mono"
            />

            {cleaned.length > 0 && cleaned.length < 15 && (
                <p className="text-sm text-muted-foreground">
                    Keep typing... ({cleaned.length}/15+ characters)
                </p>
            )}

            {cleaned.length > 34 && (
                <p className="text-sm text-muted-foreground">
                    IBAN to long
                </p>
            )}

            {isLoading && (
                <div className="flex items-center gap-2 text-muted-foreground">
                    <Loader2 className="h-5 w-5 animate-spin" />
                    <span>Validating...</span>
                </div>
            )}

            {isError && (
                <div className="flex items-center gap-3 rounded-lg border border-red-300 bg-red-50 p-4 text-red-700 dark:border-red-800 dark:bg-red-950/50 dark:text-red-400">
                    <XCircle className="h-5 w-5 shrink-0" />
                    <span>Failed to reach validation service.</span>
                </div>
            )}

            {data && <ValidationResult result={data} />}
        </div>
    );
}

function ValidationResult({ result }: { result: IbanValidationResponse }) {
    return (
        <div className="space-y-3">
            <StepPattern result={result} />
            {result.patternValid && <StepBankName result={result} />}
            {result.patternValid && <StepAccountValidation result={result} />}
        </div>
    );
}

function StepPattern({ result }: { result: IbanValidationResponse }) {
    if (!result.patternValid) {
        return (
            <div className="flex items-start gap-3 rounded-lg border border-red-300 bg-red-50 p-4 text-red-700 dark:border-red-800 dark:bg-red-950/50 dark:text-red-400">
                <XCircle className="mt-0.5 h-5 w-5 shrink-0" />
                <div>
                    {result.error && <p className="mt-1 text-sm">{result.error}</p>}
                </div>
            </div>
        );
    }

    return (
        <div className="flex items-start gap-3 rounded-lg border border-green-300 bg-green-50 p-4 text-green-700 dark:border-green-800 dark:bg-green-950/50 dark:text-green-400">
            <CheckCircle2 className="mt-0.5 h-5 w-5 shrink-0" />
            <div>
                <p className="mt-1">IBAN pattern is valid</p>
            </div>
        </div>
    );
}

function StepBankName({ result }: { result: IbanValidationResponse }) {
    if (!result.bankName) {
        return (
            <div className="flex items-start gap-3 rounded-lg border border-yellow-300 bg-yellow-50 p-4 text-yellow-700 dark:border-yellow-800 dark:bg-yellow-950/50 dark:text-yellow-400">
                <AlertTriangle className="mt-0.5 h-5 w-5 shrink-0" />
                <div>
                    <p className="font-medium">Bank not found</p>
                </div>
            </div>
        );
    }

    return (
        <div className="flex items-start gap-3 rounded-lg border border-green-300 bg-green-50 p-4 text-green-700 dark:border-green-800 dark:bg-green-950/50 dark:text-green-400">
            <CheckCircle2 className="mt-0.5 h-5 w-5 shrink-0" />
            <div>
                <p className="mt-1">Bank: {result.bankName}</p>
            </div>
        </div>
    );
}

function StepAccountValidation({ result }: { result: IbanValidationResponse }) {
    const status = result.accountNumberValidation;

    if (status === "NOT_IMPLEMENTED") {
        return (
            <div className="flex items-start gap-3 rounded-lg border border-yellow-300 bg-yellow-50 p-4 text-yellow-700 dark:border-yellow-800 dark:bg-yellow-950/50 dark:text-yellow-400">
                <AlertTriangle className="mt-0.5 h-5 w-5 shrink-0" />
                <div>
                    <p className="font-medium">Account number validation not implemented</p>
                </div>
            </div>
        );
    }

    if (status === "INVALID") {
        return (
            <div className="flex items-start gap-3 rounded-lg border border-red-300 bg-red-50 p-4 text-red-700 dark:border-red-800 dark:bg-red-950/50 dark:text-red-400">
                <XCircle className="mt-0.5 h-5 w-5 shrink-0" />
                <div>
                    <p className="font-medium">Account number invalid</p>
                </div>
            </div>
        );
    }

    return (
        <div className="flex items-start gap-3 rounded-lg border border-green-300 bg-green-50 p-4 text-green-700 dark:border-green-800 dark:bg-green-950/50 dark:text-green-400">
            <CheckCircle2 className="mt-0.5 h-5 w-5 shrink-0" />
            <div>
                <p className="font-medium">Account number valid</p>
            </div>
        </div>
    );
}
