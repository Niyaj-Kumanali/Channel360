import React, { useEffect } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate, useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { ArrowLeft } from 'lucide-react';
import { updateUserSchema, type UpdateUserFormData } from '../schemas/user.schema';
import { useUser, useUpdateUser } from '../hooks/useUsers';
import { Input } from '@/shared/components/ui/Input';
import { Button } from '@/shared/components/ui/Button';
import { Card, CardContent, CardHeader } from '@/shared/components/ui/Card';
import { apiService } from '@/shared/services/api.service';

export const UserEditPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const userId = Number(id);
  const { data: userData, isLoading: userLoading } = useUser(userId);
  const updateMutation = useUpdateUser();

  const { data: rolesData } = useQuery({
    queryKey: ['roles'],
    queryFn: () => apiService.get<any[]>('/roles'),
  });

  const { register, handleSubmit, control, formState: { errors }, reset } = useForm<UpdateUserFormData>({
    resolver: zodResolver(updateUserSchema),
    defaultValues: { roleIds: [] },
  });

  useEffect(() => {
    if (userData?.data) {
      const user = userData.data;
      reset({
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        mobileNumber: user.mobileNumber,
        roleIds: user.roles?.map((r: any) => r.id),
      });
    }
  }, [userData, reset]);

  const onSubmit = (data: UpdateUserFormData) => {
    updateMutation.mutate({ id: userId, data }, {
      onSuccess: () => navigate('/users'),
    });
  };

  if (userLoading) {
    return <div className="flex justify-center py-12"><div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary" /></div>;
  }

  const roles = rolesData?.data || [];

  return (
    <div>
      <div className="mb-6">
        <Button variant="ghost" onClick={() => navigate('/users')}>
          <ArrowLeft className="h-4 w-4 mr-2" />
          Back to Users
        </Button>
      </div>
      <Card className="max-w-2xl">
        <CardHeader>
          <h2 className="text-xl font-bold text-gray-900">Edit User</h2>
          <p className="text-gray-500 text-sm mt-1">Update user information</p>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <Input
                id="firstName"
                label="First Name"
                error={errors.firstName?.message}
                {...register('firstName')}
              />
              <Input
                id="lastName"
                label="Last Name"
                error={errors.lastName?.message}
                {...register('lastName')}
              />
            </div>
            <Input
              id="email"
              label="Email"
              type="email"
              error={errors.email?.message}
              {...register('email')}
            />
            <Input
              id="mobileNumber"
              label="Mobile Number"
              {...register('mobileNumber')}
            />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Roles</label>
              <Controller
                name="roleIds"
                control={control}
                render={({ field }) => (
                  <div className="space-y-2">
                    {roles.map((r: any) => (
                      <label key={r.id} className="flex items-center gap-2 cursor-pointer">
                        <input
                          type="checkbox"
                          value={r.id}
                          checked={field.value?.includes(r.id) ?? false}
                          onChange={(e) => {
                            const current = field.value ?? [];
                            if (e.target.checked) {
                              field.onChange([...current, r.id]);
                            } else {
                              field.onChange(current.filter((id: number) => id !== r.id));
                            }
                          }}
                          className="rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
                        />
                        <span className="text-sm text-gray-700">{r.name}</span>
                      </label>
                    ))}
                  </div>
                )}
              />
            </div>
            <div className="flex justify-end gap-3 pt-4">
              <Button variant="outline" onClick={() => navigate('/users')}>Cancel</Button>
              <Button type="submit" isLoading={updateMutation.isPending}>Save Changes</Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};
