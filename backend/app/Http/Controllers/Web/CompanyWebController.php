<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use App\Models\Company;
use Illuminate\Http\Request;
use Inertia\Inertia;

class CompanyWebController extends Controller
{
    /**
     * Display a listing of companies
     */
    public function index()
    {
        $companies = Company::orderBy('id', 'desc')->get();
        $taxes = \App\Models\Tax::all();

        return Inertia::render('Companies/Index', [
            'companies' => $companies,
            'taxes' => $taxes
        ]);
    }

    /**
     * Store a newly created company
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'name_formatted' => 'nullable|string|max:255',
            'desc' => 'nullable|string',
            'taxation_type' => 'nullable|integer',
            'country' => 'nullable|string|max:50',
            'state' => 'nullable|string|max:50',
            'default_tax_id' => 'nullable|integer|exists:taxes,id',
            'tax_application_level' => 'nullable|string',
            'tax_number' => 'nullable|string|max:20',
            'address' => 'nullable|string',
            'call' => 'nullable|string',
            'whatsapp' => 'nullable|string',
            'footer_content' => 'nullable|string',
            'signature' => 'sometimes|boolean',
            'financial_year_start' => 'nullable|date',
            'enable_delivery_notes' => 'sometimes|boolean',
            'enable_grns' => 'sometimes|boolean',
            'allow_negative_stock' => 'sometimes|boolean',
            'contacts' => 'nullable|array',
            'contacts.*.name' => 'required_with:contacts|string|max:255',
            'contacts.*.phone' => 'nullable|string|max:50',
            'contacts.*.email' => 'nullable|email|max:255',
            'contacts.*.designation' => 'nullable|string|max:100',
            'contacts.*.is_primary' => 'sometimes|boolean',
        ]);

        $validated['signature'] = $request->boolean('signature');
        $validated['enable_delivery_notes'] = $request->has('enable_delivery_notes') ? $request->boolean('enable_delivery_notes') : true;
        $validated['enable_grns'] = $request->has('enable_grns') ? $request->boolean('enable_grns') : true;
        $validated['allow_negative_stock'] = $request->boolean('allow_negative_stock');
        $validated['tax_application_level'] = $validated['tax_application_level'] ?? 'item';

        $company = Company::create($validated);

        if ($request->has('contacts')) {
            foreach ($request->input('contacts') as $contact) {
                if (!empty($contact['name'])) {
                    $company->contacts()->create([
                        'name' => $contact['name'],
                        'phone' => $contact['phone'] ?? null,
                        'email' => $contact['email'] ?? null,
                        'designation' => $contact['designation'] ?? null,
                        'is_primary' => filter_var($contact['is_primary'] ?? false, FILTER_VALIDATE_BOOLEAN),
                    ]);
                }
            }
        }

        // Auto-create default location
        \App\Models\Location::create([
            'company_id' => $company->id,
            'name' => 'Main Location',
            'type' => 'warehouse',
            'is_default' => true,
            'is_active' => true,
            'address' => $company->address,
            'city' => null,
            'state' => $company->state,
            'country' => $company->country,
            'tax_number' => $company->tax_number,
        ]);

        return back()->with('success', 'Company created successfully');
    }

    /**
     * Update the specified company
     */
    public function update(Request $request, $id)
    {
        $company = Company::findOrFail($id);

        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'name_formatted' => 'nullable|string|max:255',
            'desc' => 'nullable|string',
            'taxation_type' => 'nullable|integer',
            'country' => 'nullable|string|max:50',
            'state' => 'nullable|string|max:50',
            'default_tax_id' => 'nullable|integer|exists:taxes,id',
            'tax_application_level' => 'nullable|string',
            'tax_number' => 'nullable|string|max:20',
            'address' => 'nullable|string',
            'call' => 'nullable|string',
            'whatsapp' => 'nullable|string',
            'footer_content' => 'nullable|string',
            'signature' => 'sometimes|boolean',
            'financial_year_start' => 'nullable|date',
            'enable_delivery_notes' => 'sometimes|boolean',
            'enable_grns' => 'sometimes|boolean',
            'allow_negative_stock' => 'sometimes|boolean',
            'contacts' => 'nullable|array',
            'contacts.*.name' => 'required_with:contacts|string|max:255',
            'contacts.*.phone' => 'nullable|string|max:50',
            'contacts.*.email' => 'nullable|email|max:255',
            'contacts.*.designation' => 'nullable|string|max:100',
            'contacts.*.is_primary' => 'sometimes|boolean',
        ]);

        $validated['signature'] = $request->boolean('signature');
        $validated['enable_delivery_notes'] = $request->boolean('enable_delivery_notes');
        $validated['enable_grns'] = $request->boolean('enable_grns');
        $validated['allow_negative_stock'] = $request->boolean('allow_negative_stock');

        $company->update($validated);

        // Sync contacts
        if ($request->has('contacts')) {
            $existingIds = [];
            foreach ($request->input('contacts') as $contactData) {
                if (empty($contactData['name']))
                    continue;

                $data = [
                    'name' => $contactData['name'],
                    'phone' => $contactData['phone'] ?? null,
                    'email' => $contactData['email'] ?? null,
                    'designation' => $contactData['designation'] ?? null,
                    'is_primary' => filter_var($contactData['is_primary'] ?? false, FILTER_VALIDATE_BOOLEAN),
                ];

                if (isset($contactData['id'])) {
                    $contact = $company->contacts()->find($contactData['id']);
                    if ($contact) {
                        $contact->update($data);
                        $existingIds[] = $contact->id;
                    }
                } else {
                    $newContact = $company->contacts()->create($data);
                    $existingIds[] = $newContact->id;
                }
            }
            // Delete removed contacts
            $company->contacts()->whereNotIn('id', $existingIds)->delete();
        }

        return redirect()->back()->with('success', 'Company updated successfully');
    }

    /**
     * Remove the specified company
     */
    public function destroy($id)
    {
        $company = Company::findOrFail($id);
        $company->delete();

        return redirect()->route('companies.index')
            ->with('success', 'Company deleted successfully');
    }
}
